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
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
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

	@Column(name = "member_type", nullable = false, length = 50)
	private String memberType;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(name = "registered_date", nullable = false)
	private LocalDate registeredDate;

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

	public Member() {

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(LocalDate registeredDate) {
		this.registeredDate = registeredDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public List<BorrowTransaction> getBorrowTransactions() {
		return borrowTransactions;
	}

	public void setBorrowTransactions(List<BorrowTransaction> borrowTransactions) {
		this.borrowTransactions = borrowTransactions;
	}

	public List<BookHistory> getBookHistories() {
		return bookHistories;
	}

	public void setBookHistories(List<BookHistory> bookHistories) {
		this.bookHistories = bookHistories;
	}

	@Override
	public String toString() {
		return "Member{" + "id=" + id + ", memberCode='" + memberCode + '\'' + ", name='" + name + '\'' + ", phone='"
				+ phone + '\'' + ", email='" + email + '\'' + ", address='" + address + '\'' + ", memberType='"
				+ memberType + '\'' + ", status='" + status + '\'' + ", registeredDate=" + registeredDate
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
	}
}