package com.library.management.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "book_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "book_id", nullable = false)
	@ToString.Exclude
	private Book book;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	@ToString.Exclude
	private Member member;

	@ManyToOne
	@JoinColumn(name = "borrow_transaction_id", nullable = false)
	@ToString.Exclude
	private BorrowTransaction borrowTransaction;

	@Column(name = "borrow_date", nullable = false)
	private LocalDate borrowDate;

	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	@Column(name = "return_date")
	private LocalDate returnDate;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(length = 255)
	private String remarks;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// Custom constructor matching your original manual constructor (excluding id and createdAt)
	public BookHistory(Book book, Member member, BorrowTransaction borrowTransaction, LocalDate borrowDate,
			LocalDate dueDate, LocalDate returnDate, String status, String remarks) {
		this.book = book;
		this.member = member;
		this.borrowTransaction = borrowTransaction;
		this.borrowDate = borrowDate;
		this.dueDate = dueDate;
		this.returnDate = returnDate;
		this.status = status;
		this.remarks = remarks;
	}
}