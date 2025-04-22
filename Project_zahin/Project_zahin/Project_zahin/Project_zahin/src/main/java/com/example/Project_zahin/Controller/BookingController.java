package com.example.Project_zahin.Controller;

import com.example.Project_zahin.models.Booking;
import com.example.Project_zahin.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    // Show booking form
    @GetMapping("/new")
    public String showBookingForm(@RequestParam String location, Model model) {
        model.addAttribute("location", location);
        return "HotelBooking";
    }

    // Save new booking
    @PostMapping("/save")
    public String saveBooking(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String location,
            @RequestParam String checkin,
            @RequestParam String checkout,
            @RequestParam String roomType,
            @RequestParam int guests,
            Model model) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate checkinDate = LocalDate.parse(checkin, formatter);
            LocalDate checkoutDate = LocalDate.parse(checkout, formatter);

            // Validate dates
            if (checkoutDate.isBefore(checkinDate) || checkoutDate.isEqual(checkinDate)) {
                model.addAttribute("error", "Check-out date must be after check-in date");
                model.addAttribute("location", location);
                return "HotelBooking";
            }

            Booking booking = new Booking();
            booking.setFullName(fullName);
            booking.setPhone(phone);
            booking.setLocation(getFullLocationName(location));
            booking.setCheckin(checkinDate);
            booking.setCheckout(checkoutDate);
            booking.setRoomType(roomType);
            booking.setGuests(guests);
            booking.setBookingDate(LocalDate.now());
            booking.setStatus("CONFIRMED");

            // Generate booking reference
            String bookingRef = "RL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            booking.setBookingReference(bookingRef);

            Booking savedBooking = bookingRepository.save(booking);

            // Format dates for display
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

            // Add attributes for confirmation page
            model.addAttribute("bookingRef", bookingRef);
            model.addAttribute("fullName", savedBooking.getFullName());
            model.addAttribute("phone", savedBooking.getPhone());
            model.addAttribute("location", savedBooking.getLocation());
            model.addAttribute("checkin", displayFormatter.format(savedBooking.getCheckin()));
            model.addAttribute("checkout", displayFormatter.format(savedBooking.getCheckout()));
            model.addAttribute("roomType", savedBooking.getRoomType());
            model.addAttribute("guests", savedBooking.getGuests());
            model.addAttribute("bookingDate", displayFormatter.format(savedBooking.getBookingDate()));

            return "saveBooking";
        } catch (Exception e) {
            model.addAttribute("error", "Error processing booking: " + e.getMessage());
            model.addAttribute("location", location);
            return "HotelBooking";
        }
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
        model.addAttribute("booking", booking);
        return "editBooking";
    }

    // Update booking - Corrected version
    @PostMapping("/update/{id}")
    public String updateBooking(@PathVariable Long id, @ModelAttribute Booking booking,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "editBooking";
        }

        // Get existing booking
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));

        // Update only editable fields
        existingBooking.setFullName(booking.getFullName());
        existingBooking.setPhone(booking.getPhone());
        existingBooking.setRoomType(booking.getRoomType());
        existingBooking.setGuests(booking.getGuests());
        existingBooking.setStatus(booking.getStatus());

        // Save the updated booking
        bookingRepository.save(existingBooking);
        return "redirect:/admin/booking-list";
    }

    // Cancel booking
    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        return "redirect:/admin/booking-list";
    }

    private String getFullLocationName(String locationCode) {
        switch (locationCode) {
            case "kuala-lumpur":
                return "Royale Laurent Kuala Lumpur";
            case "penang":
                return "Royale Laurent Penang";
            case "bali":
                return "Royale Laurent Bali";
            case "san-diego":
                return "Royale Laurent San Diego";
            case "oslo":
                return "Royale Laurent Oslo";
            default:
                return locationCode;
        }
    }
}