package com.nmquan1503.backend_springboot.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(1000, "Success."),

    PHONE_EMPTY(2001,"Phone number is required. Please enter a valid phone number."),
    EMAIL_EMPTY(2002, "Email is required. Please enter a valid email address."),
    PASSWORD_EMPTY(2003, "Password is required. Please enter a valid password."),
    GENDER_EMPTY(2004, "Gender is required. Please choose a valid gender."),
    BIRTHDAY_EMPTY(2005, "Birthday is required. Please enter your birthday."),
    FULLNAME_EMPTY(2006, "Full name is required. Please enter your full name."),
    ADDRESS_EMPTY(2007, "Address is required. Please enter your address."),
    EMAIL_OR_PHONE_EMPTY(2008, "Email/phone number is required. Please enter a valid email/phone number."),
    TITLE_MOVIE_EMPTY(2009, "Title's movie is required."),
    POSTER_MOVIE_EMPTY(2010, "Poster's movie is required."),
    RELEASED_DATE_MOVIE_EMPTY(2011, "Released date's movie is required."),
    DURATION_MOVIE_EMPTY(2012, "Duration's movie is required."),
    ORIGINAL_LANGUAGE_MOVIE_EMPTY(2013, "Original language's movie is required."),
    MOVIE_EMPTY(2014, "Movie is required."),
    RATING_REVIEW_EMPTY(2015, "Rating's review is required."),
    COMMENT_REVIEW_EMPTY(2016, "Comment's review is required."),
    BRANCH_EMPTY(2017, "Branch is required."),
    PRODUCT_EMPTY(2018, "Product is required."),
    PRODUCT_STATUS_EMPTY(2019, "Product status is required."),
    PRODUCT_NAME_EMPTY(2020, "Product's name is required."),
    PRODUCT_THUMBNAIL_EMPTY(2021, "Product's thumbnail is required."),
    PRODUCT_PRICE_EMPTY(2022, "Product's price is required."),
    PRODUCT_QUANTITY_EMPTY(2023, "Product's quantity is required."),
    SHOWTIME_EMPTY(2024, "Showtime is required."),
    SEAT_EMPTY(2025, "Seat is required."),
    START_TIME_SHOWTIME(2026, "Start time's showtime is required."),

    PHONE_INVALID(3001,"The phone number you entered is not valid. Please check and try again."),
    EMAIL_INVALID(3002, "The email you entered is not valid. Please check and try again."),
    FULLNAME_INVALID(3003, "The full name you entered is not valid. Please check and try again."),
    BIRTHDAY_INVALID(3004, "The birthday you entered is not valid. Please check and try again."),
    SIGNATURE_KEY_INVALID(3005, "The signature key is invalid or unable to create MACVerifier."),
    CAST_ORDER_INVALID(3004, "The cast's order is not valid."),

    PHONE_USER_EXISTED(4001, "This phone number is already in use. Please try another."),
    EMAIL_USER_EXISTED(4002, "This email is already in use. Please try another."),
    PHONE_OR_EMAIL_USER_EXISTED(4003, "Phone or email is already in use. Please try another."),

    USER_NOT_FOUND(5001, "User does not exist in the system."),
    GENDER_NOT_FOUND(5002, "Gender does not exist in the system."),
    PROVINCE_NOT_FOUND(5003, "Province does not exist in the system."),
    DISTRICT_NOT_FOUND(5004, "District does not exist in the system."),
    WARD_NOT_FOUND(5005, "Ward does not exist in the system."),
    PERMISSION_NOT_FOUND(5006, "Permission does not exist in the system."),
    ROLE_NOT_FOUND(5007, "Role does not exist in the system."),
    MOVIE_NOT_FOUND(5008, "Movie does not exist in the system."),
    LANGUAGE_NOT_FOUND(5009, "Movie does not exist in the system."),
    AGE_RATING_NOT_FOUND(5010, "Age rating does not exist in the system."),
    CATEGORY_NOT_FOUND(5011, "Category does not exist in the system."),
    PERSON_NOT_FOUND(5012, "Person does not exist in the system."),
    MOVIE_REVIEW_NOT_FOUND(5013, "Movie review does not exist in the system."),
    SHOWTIME_NOT_FOUND(5014, "Showtime does not exist in the system."),
    RESERVATION_STATUS_NOT_FOUND(5015, "Reservation status does not exist in the system."),
    SEAT_NOT_FOUND(5016, "Reservation status does not exist in the system."),
    PRODUCT_NOT_FOUND(5017, "Product does not exist in the system."),
    RESERVATION_NOT_FOUND(5018, "Reservation does not exist in the system."),
    PAYMENT_METHOD_NOT_FOUND(5019, "Payment method does not exist in the system."),
    PAYMENT_TRANSACTION_STATUS_NOT_FOUND(5020, "Payment transaction status does not exist in the system."),
    PAYMENT_TRANSACTION_NOT_FOUND(5021, "Payment transaction does not exist in the system."),
    TICKET_STATUS_NOT_FOUND(5022, "Ticket status does not exist in the system."),
    TICKET_NOT_FOUND(5023, "Ticket does not exist in the system."),
    BRANCH_NOT_FOUND(5024, "Branch does not exist in the system."),
    PRODUCT_STATUS_NOT_FOUND(5025, "Product status does not exist in the system."),
    BRANCH_PRODUCT_NOT_FOUND(5026, "Branch-product does not exist in the system."),
    ROOM_NOT_FOUND(5027, "Room does not exist in the system."),
    SHOWTIME_STATUS_NOT_FOUND(5028, "Showtime status does not exist in the system."),
    ROOM_TYPE_NOT_FOUND(5029, "Room type does not exist in the system."),
    POSITION_NOT_FOUND(5030, "Position does not exist in the system."),
    MOVIE_CAST_NOT_FOUND(5031, "Movie-cast does not exist in the system."),
    MOVIE_CREW_NOT_FOUND(5032, "Movie-crew does not exist in the system."),

    SEAT_NOT_AVAILABLE(6001, "Seats are not available."),
    SHOWTIME_NOT_AVAILABLE(6002, "Showtime is not available."),

    DUPLICATE_ELEMENT(7001, "The collection contains duplicate elements."),
    DUPLICATE_ACTOR(7002, "The list contains duplicate actors."),
    DUPLICATE_DIRECTOR(7003, "The list contains duplicate directors."),
    DUPLICATE_CATEGORY(7004, "The list contains duplicate categories"),
    DUPLICATE_SEAT(7005, "The list contains duplicate seats."),
    DUPLICATE_CAST_ORDER(7006, "The list contains duplicate cast order."),
    DUPLICATE_MOVIE_CAST(7007, "The list contains duplicate movie-cast."),
    DUPLICATE_MOVIE_CREW(7008, "The list contains duplicate movie-crew."),
    DUPLICATE_CAST_MEMBER(7009, "The list contains duplicate cast members."),
    DUPLICATE_CREW_MEMBER(7010, "The list contains duplicate crew members."),
    DUPLICATE_IMAGE(7011, "The list contains duplicate images."),

    AUTHENTICATED (8001, "Authenticated."),
    UNAUTHENTICATED (8002, "Unauthenticated."),
    UNAUTHORIZED (8003, "You don't have permission."),
    SHOWTIME_HAS_BOOKINGS(9987, "Lịch chiếu này đã có khách đặt vé, không thể xóa."),

    PAYMENT_TRANSACTION_PAID(9988, "Payment transaction was paid."),
    SCHEDULE_CONFLICT(9989, "Showtime conflicts with an existing schedule."),
    PAYMENT_METHOD_DISABLED(9990, "Unsupported payment method."),
    PAYMENT_METHOD_MAINTENANCE(9991, "Payment method under maintenance."),
    UNPAYABLE_RESERVATION(9992, "This reservation cannot be paid."),
    RESERVATION_EXPIRED(9993, "The reservation has expired."),
    RESERVATION_ALREADY_PAID(9994, "Reservation has already been paid."),
    PAYMENT_FAILED(9995, "Payment failed."),
    TOKEN_SIGNING_ERROR(9996, "Failed to sign JWT."),
    TYPE_MISMATCH(9997, "Invalid JSON or data type mismatch"),
    INVALID_ENUM_KEY(9998, "Invalid enum key."),
    UNKNOWN_ERROR(9999, "Unknown error.");

    int code;
    String message;

}