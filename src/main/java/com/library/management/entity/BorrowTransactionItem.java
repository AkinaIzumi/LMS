package com.library.management.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "borrow_transaction_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowTransactionItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "borrow_transaction_id", nullable = false)
	@ToString.Exclude
	private BorrowTransaction borrowTransaction;

	@ManyToOne
	@JoinColumn(name = "book_id", nullable = false)
	@ToString.Exclude
	private Book book;

	@Column(name = "return_date")
	private LocalDate returnDate;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(length = 255)
	private String remarks;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Custom constructor matching your original manual constructor (excluding id and timestamps)
	public BorrowTransactionItem(BorrowTransaction borrowTransaction, Book book, LocalDate returnDate, String status,
			String remarks) {
		this.borrowTransaction = borrowTransaction;
		this.book = book;
		this.returnDate = returnDate;
		this.status = status;
		this.remarks = remarks;
	}
}