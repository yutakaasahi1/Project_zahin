package com.example.Project_zahin.Controller;

import com.example.Project_zahin.models.Booking;
import com.example.Project_zahin.repository.BookingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AdminController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/admin/booking-list")
    public String showBookingList(Model model) {
        List<Booking> bookings = bookingRepository.findAll();

        // Format dates for display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bookings.forEach(booking -> {
            if (booking.getStatus() == null) {
                booking.setStatus("CONFIRMED"); // Default status
            }
        });

        model.addAttribute("bookings", bookings);
        return "bookingList";
    }

    @GetMapping("/admin/room-management")
    public String showRoomManagement() {
        return "roomManagement";
    }

    @GetMapping("/admin/search-booking")
    public String showSearchBooking(
            @RequestParam(required = false) String bookingId,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) String status,
            Model model) {

        // Create specification for dynamic query
        Specification<Booking> spec = Specification.where(null);

        if (bookingId != null && !bookingId.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("bookingReference"), "%" + bookingId + "%"));
        }

        if (guestName != null && !guestName.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("fullName")), "%" + guestName.toLowerCase() + "%"));
        }

        if (checkInDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("checkin"), checkInDate));
        }

        if (checkOutDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("checkout"), checkOutDate));
        }

        if (roomType != null && !roomType.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("roomType"), roomType));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
        }

        List<Booking> bookings = bookingRepository.findAll(spec);
        model.addAttribute("bookings", bookings);

        return "searchBooking";
    }

    @GetMapping("/admin/register")
    public String showRegisterAdmin() {
        return "register";
    }
}