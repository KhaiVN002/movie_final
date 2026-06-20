import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminLookupOption, AdminManagementService } from '../../services/admin-management.service';

interface UserAccount {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  role: string;
  roles: string;
  status: 'ACTIVE' | 'LOCKED';
  createdDate: string;
}

@Component({
  standalone: true,
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class AdminUsersComponent implements OnInit {
  users: UserAccount[] = [];
  filteredUsers: UserAccount[] = [];
  roles: AdminLookupOption[] = [];

  searchQuery = '';
  roleFilter = 'ALL';
  page = 0;
  size = 20;
  totalPages = 0;
  totalElements = 0;
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private adminService: AdminManagementService) {}

  ngOnInit(): void {
    this.loadRoles();
    this.loadUsers();
  }

  loadRoles(): void {
    this.adminService.getLookups().subscribe({
      next: (res) => {
        if (res.success) {
          this.roles = res.data?.find(group => group.key === 'roles')?.options || [];
        }
      }
    });
  }

  loadUsers(): void {
    this.isLoading = true;
    this.errorMessage = '';
    const roleId = this.roleFilter === 'ALL'
      ? undefined
      : this.roles.find(role => role.name === this.roleFilter)?.id;

    this.adminService.getPage<any>('users', {
      page: this.page,
      size: this.size,
      keyword: this.searchQuery,
      roleId,
      sort: 'creationTime',
      direction: 'DESC'
    }).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.users = (res.data.content || []).map((u: any) => ({
            id: u.id,
            fullName: u.fullName || 'User Account',
            email: u.email || '',
            phone: u.phone || '',
            role: this.firstRole(u.roles),
            roles: u.roles || '',
            status: 'ACTIVE',
            createdDate: u.creationTime ? new Date(u.creationTime).toLocaleDateString() : ''
          }));
          this.totalElements = res.data.totalElements || 0;
          this.totalPages = res.data.totalPages || 0;
          this.applyFilters();
        } else {
          this.errorMessage = res.message || 'Failed to load users.';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Cannot load users from admin API.';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredUsers = this.users;
  }

  onSearchChange(): void {
    this.page = 0;
    this.loadUsers();
  }

  onRoleFilterChange(role: string): void {
    this.roleFilter = role;
    this.page = 0;
    this.loadUsers();
  }

  toggleLock(user: UserAccount): void {
    user.status = user.status === 'ACTIVE' ? 'LOCKED' : 'ACTIVE';
    this.showToast(`Local access marker for ${user.fullName} is now ${user.status}.`);
    this.applyFilters();
  }

  changeRole(user: UserAccount, newRole: string): void {
    if (user.role === newRole) return;
    const role = this.roles.find(item => item.name === newRole);
    if (!role) {
      this.errorMessage = 'Role is not available in lookup settings.';
      return;
    }

    if (confirm(`Change role of ${user.fullName} from ${user.role} to ${newRole}?`)) {
      this.adminService.updateUserRoles(user.id, [role.id]).subscribe({
        next: (res) => {
          if (res.success) {
            user.role = newRole;
            user.roles = newRole;
            this.showToast(`Role of ${user.fullName} updated to ${newRole}.`);
            this.applyFilters();
          } else {
            this.errorMessage = res.message || 'Role update failed.';
          }
        },
        error: () => {
          this.errorMessage = 'Role update failed.';
        }
      });
    }
  }

  showToast(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => this.successMessage = '', 3000);
  }

  private firstRole(roles: string | null | undefined): string {
    if (!roles) {
      return 'USER';
    }
    return roles.split(',')[0].trim() || 'USER';
  }
}
