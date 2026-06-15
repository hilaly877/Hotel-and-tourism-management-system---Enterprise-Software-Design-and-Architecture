const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema(
    {
        guestId: {
            type: Number,
            required: [true, 'Guest ID is required']
        },
        bookingId: {
            type: Number,
            default: null     // Optional — some notifications are not booking-related
        },
        type: {
            type: String,
            enum: ['EMAIL', 'SMS'],
            required: [true, 'Notification type is required']
        },
        subject: {
            type: String,
            required: [true, 'Subject is required'],
            trim: true
        },
        message: {
            type: String,
            required: [true, 'Message is required'],
            trim: true
        },
        recipientEmail: {
            type: String,
            trim: true,
            lowercase: true,
            // Required only for EMAIL type — validated in controller
        },
        recipientPhone: {
            type: String,
            trim: true,
            // Required only for SMS type — validated in controller
        },
        status: {
            type: String,
            enum: ['SENT', 'FAILED', 'PENDING'],
            default: 'PENDING'
        },
        category: {
            type: String,
            enum: [
                'BOOKING_CONFIRMATION',
                'BOOKING_CANCELLATION',
                'PAYMENT_SUCCESS',
                'PAYMENT_REFUND',
                'CHECK_IN_REMINDER',
                'CHECK_OUT_REMINDER',
                'GENERAL'
            ],
            default: 'GENERAL'
        },
        errorMessage: {
            type: String,
            default: null   // Stores error details if sending failed
        },
        sentAt: {
            type: Date,
            default: null
        }
    },
    {
        timestamps: true   // Adds createdAt and updatedAt automatically
    }
);

module.exports = mongoose.model('Notification', notificationSchema);
