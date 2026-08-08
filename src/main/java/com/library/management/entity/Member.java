package com.library.management.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "member_code", nullable = false, unique = true, length = 20)
	private String memberCode;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(length = 255)
	private String address;

	@Column(name = "loss_book_count")
    private Integer lossBookCount = 0;

	@Column(name = "member_type", nullable = false, length = 50)
	private String memberType;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(name = "registered_date", nullable = false)
	private LocalDate registeredDate;

	@Column(name = "expired_date") 
    private LocalDate expiredDate;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "member")
	private List<BorrowTransaction> borrowTransactions;

	@OneToMany(mappedBy = "member")
	private List<BookHistory> bookHistories;

	@PrePersist
    public void prePersist() {
        if (this.memberCode == null || this.memberCode.isEmpty()) {
            this.memberCode = "MEM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }
        if (this.status == null || this.status.isEmpty()) {
            this.status = "active";
        }
        if (this.lossBookCount == null) {
            this.lossBookCount = 0;
        }
        if (this.registeredDate == null) {
            this.registeredDate = LocalDate.now();
        }
        if (this.expiredDate == null) {
            this.expiredDate = this.registeredDate.plusYears(1); 
        }
    }

    public Member(String memberCode, String name, String phone, String email, String address, String memberType,
            String status, LocalDate registeredDate) {
        super();
        this.memberCode = memberCode;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.memberType = memberType;
        this.status = status;
        this.registeredDate = registeredDate;
    }

    @Override
    public String toString() {
        return "Member{" + "id=" + id + ", memberCode='" + memberCode + '\'' + ", name='" + name + '\'' + ", phone='"
                + phone + '\'' + ", email='" + email + '\'' + ", address='" + address + '\'' + ", memberType='"
                + memberType + '\'' + ", status='" + status + '\'' + ", registeredDate=" + registeredDate
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }
}