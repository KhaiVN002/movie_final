package com.nmquan1503.backend_springboot.services.payment;

import com.nmquan1503.backend_springboot.dtos.internal.PaymentCreationRequest;
import com.nmquan1503.backend_springboot.dtos.internal.PaymentCallbackResult;
import com.nmquan1503.backend_springboot.dtos.internal.PaymentInitResult;
import com.nmquan1503.backend_springboot.dtos.requests.payment.PaymentTransactionCreationRequest;
import com.nmquan1503.backend_springboot.dtos.responses.payment.PaymentRedirectResponse;
import com.nmquan1503.backend_springboot.entities.payment.PaymentTransaction;
import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.repositories.payment.PaymentTransactionRepository;
import com.nmquan1503.backend_springboot.services.authentication.AuthenticationService;
import com.nmquan1503.backend_springboot.services.reservation.ReservationService;
import com.nmquan1503.backend_springboot.services.ticket.TicketService;
import com.nmquan1503.backend_springboot.utils.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentTransactionService {

    PaymentTransactionRepository paymentTransactionRepository;

    PaymentStrategyFactory paymentStrategyFactory;

    AuthenticationService authenticationService;
    ReservationService reservationService;
    PaymentTransactionStatusService paymentTransactionStatusService;
    TicketService ticketService;

    public void save(PaymentTransaction paymentTransaction) {
        paymentTransactionRepository.save(paymentTransaction);
    }

    public PaymentRedirectResponse processPayment(HttpServletRequest httpServletRequest, PaymentTransactionCreationRequest request) {
        Long userId = authenticationService.getCurrentUserId();
        Reservation reservation = reservationService.fetchById(request.getReservationId());
        if (!reservation.getUser().getId().equals(userId)) {
            throw new GeneralException(ResponseCode.UNAUTHORIZED);
        }
        if (reservation.getStatus().getName().equals("PAID")) {
            throw new GeneralException(ResponseCode.RESERVATION_ALREADY_PAID);
        }
        else if (reservation.getStatus().getName().equals("EXPIRED") || reservation.getEndTime().isBefore(LocalDateTime.now())) {
            throw new GeneralException(ResponseCode.RESERVATION_EXPIRED);
        }
        else if (!reservation.getStatus().getName().equals("PENDING")) {
            throw new GeneralException(ResponseCode.UNPAYABLE_RESERVATION);
        }
        long amount = (long) reservationService.getTotalAmountByReservation(reservation);
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getPaymentStrategy(request.getPaymentMethodId());
        PaymentCreationRequest paymentCreationRequest = PaymentCreationRequest.builder()
                .reservationId(request.getReservationId())
                .amount(amount)
                .expireTime(reservation.getEndTime())
                .ipAddress(VnPayUtil.getIpAddress(httpServletRequest))
                .build();
        PaymentInitResult result = paymentStrategy.processPayment(paymentCreationRequest);

        paymentTransactionRepository.save(result.getPaymentTransaction());
        return PaymentRedirectResponse.builder()
                .redirectURL(result.getPaymentURL())
                .build();
    }

    @Transactional
    public boolean callbackHandler(Map<String, String> params) {
        PaymentStrategy paymentStrategy = paymentStrategyFactory.detectPaymentStrategy(params);
        PaymentCallbackResult result = paymentStrategy.handleCallback(params);
        if (!result.isSuccess()) {
            return false;
        }
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByTransactionId(result.getTransactionId())
                .orElseThrow(() -> new GeneralException(ResponseCode.PAYMENT_TRANSACTION_NOT_FOUND));
        if (paymentTransaction.getStatus().getName().equals("SUCCESS")) {
            // throw new GeneralException(ResponseCode.PAYMENT_TRANSACTION_PAID);
            return true;
        }
        paymentTransaction.setStatus(paymentTransactionStatusService.fetchByName("SUCCESS"));
        paymentTransactionRepository.save(paymentTransaction);
        reservationService.markReservationAsPaid(paymentTransaction.getReservation());
        ticketService.createTicket(paymentTransaction.getReservation());
        return true;
    }
}
