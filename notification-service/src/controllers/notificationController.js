const Notification = require('../models/Notification');
const axios = require('axios');
const emailTransporter = require('../config/emailClient');

const GUEST_SERVICE_URL = process.env.GUEST_SERVICE_URL || 'http://localhost:8081';

// ─── SEND REAL EMAIL VIA NODEMAILER SMTP ──────────────────────────────────
const sendEmail = async (to, subject, message) => {
    try {
        console.log(`📧 Sending email via SMTP to: ${to}`);

        const mailOptions = {
            from: process.env.EMAIL_FROM,
            to: to,
            subject: subject,
            text: message,
            html: `
                <div style="font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px;">
                    <div style="background: white; padding: 30px; border-radius: 8px; max-width: 600px; margin: 0 auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                        <h2 style="color: #1F5C99; text-align: center; margin-top: 0;">
                            🏨 Hotel Management System
                        </h2>
                        <hr style="border: none; border-top: 2px solid #1F5C99; margin: 20px 0;">
                        <div style="font-size: 14px; line-height: 1.8; color: #333;">
                            ${message}
                        </div>
                        <hr style="border: none; border-top: 1px solid #ddd; margin: 20px 0;">
                        <p style="color: #999; font-size: 12px; text-align: center; margin-bottom: 0;">
                            This is an automated message from Hotel Management System.<br>
                            Please do not reply to this email.
                        </p>
                    </div>
                </div>
            `
        };

        // Send email
        const info = await emailTransporter.sendMail(mailOptions);

        console.log(`✅ Email sent successfully!`);
        console.log(`   Recipient: ${to}`);
        console.log(`   Message ID: ${info.messageId}`);

        return true;

    } catch (error) {
        console.error(`❌ Email send failed:`, error.message);
        throw error;
    }
};

// ─── SIMULATE SMS (No Twilio) ──────────────────────────────────────────────
const sendSMS = async (phone, message) => {
    console.log(`📱 [SIMULATED SMS - Twilio not configured]`);
    console.log(`   To: ${phone}`);
    console.log(`   Message: ${message}`);
    return true;
};

// ─── GET ALL NOTIFICATIONS ────────────────────────────────────────────────
const getAllNotifications = async (req, res, next) => {
    try {
        const notifications = await Notification.find().sort({ createdAt: -1 });
        res.status(200).json({
            count: notifications.length,
            notifications
        });
    } catch (err) {
        next(err);
    }
};

// ─── GET NOTIFICATION BY ID ───────────────────────────────────────────────
const getNotificationById = async (req, res, next) => {
    try {
        const notification = await Notification.findById(req.params.id);
        if (!notification) {
            return res.status(404).json({
                error: `Notification not found with ID: ${req.params.id}`
            });
        }
        res.status(200).json(notification);
    } catch (err) {
        next(err);
    }
};

// ─── GET BY GUEST ─────────────────────────────────────────────────────────
const getNotificationsByGuest = async (req, res, next) => {
    try {
        const notifications = await Notification
            .find({ guestId: req.params.guestId })
            .sort({ createdAt: -1 });
        res.status(200).json({
            guestId: req.params.guestId,
            count: notifications.length,
            notifications
        });
    } catch (err) {
        next(err);
    }
};

// ─── GET BY BOOKING ───────────────────────────────────────────────────────
const getNotificationsByBooking = async (req, res, next) => {
    try {
        const notifications = await Notification
            .find({ bookingId: req.params.bookingId })
            .sort({ createdAt: -1 });
        res.status(200).json({
            bookingId: req.params.bookingId,
            count: notifications.length,
            notifications
        });
    } catch (err) {
        next(err);
    }
};

// ─── SEND EMAIL NOTIFICATION ──────────────────────────────────────────────
const sendEmailNotification = async (req, res, next) => {
    try {
        const { guestId, bookingId, subject, message, recipientEmail, category } = req.body;

        // Validate required fields
        if (!guestId || !subject || !message || !recipientEmail) {
            return res.status(400).json({
                error: 'Validation Failed',
                details: 'guestId, subject, message, recipientEmail are required'
            });
        }

        // Try to verify guest — if Guest Service down, continue anyway (independent service)
        try {
            await axios.get(`${GUEST_SERVICE_URL}/api/guests/${guestId}`);
            console.log(`✅ Guest ${guestId} verified`);
        } catch (err) {
            if (err.response && err.response.status === 404) {
                return res.status(400).json({
                    error: 'Invalid guestId',
                    message: `Guest with ID ${guestId} not found`
                });
            }
            // Guest Service is down — log warning and continue
            console.log('⚠️ Guest Service unavailable — skipping guest verification');
        }

        // Save notification as PENDING
        const notification = new Notification({
            guestId,
            bookingId: bookingId || null,
            type: 'EMAIL',
            subject,
            message,
            recipientEmail,
            category: category || 'GENERAL',
            status: 'PENDING'
        });

        // Send real email
        try {
            await sendEmail(recipientEmail, subject, message);
            notification.status = 'SENT';
            notification.sentAt = new Date();
        } catch (sendError) {
            notification.status = 'FAILED';
            notification.errorMessage = sendError.message;
            console.error('❌ Failed to send email');
        }

        // Save to MongoDB
        await notification.save();

        res.status(201).json({
            message: `Email notification ${notification.status.toLowerCase()}`,
            notification,
            recipient: recipientEmail,
            timestamp: new Date()
        });

    } catch (err) {
        next(err);
    }
};

// ─── SEND SMS NOTIFICATION ────────────────────────────────────────────────
const sendSmsNotification = async (req, res, next) => {
    try {
        const { guestId, bookingId, subject, message, recipientPhone, category } = req.body;

        if (!guestId || !subject || !message || !recipientPhone) {
            return res.status(400).json({
                error: 'Validation Failed',
                details: 'guestId, subject, message, recipientPhone are required'
            });
        }

        // Try to verify guest
        try {
            await axios.get(`${GUEST_SERVICE_URL}/api/guests/${guestId}`);
        } catch (err) {
            if (err.response && err.response.status === 404) {
                return res.status(400).json({
                    error: 'Invalid guestId',
                    message: `Guest with ID ${guestId} not found`
                });
            }
            console.log('⚠️ Guest Service unavailable — skipping verification');
        }

        const notification = new Notification({
            guestId,
            bookingId: bookingId || null,
            type: 'SMS',
            subject,
            message,
            recipientPhone,
            category: category || 'GENERAL',
            status: 'PENDING'
        });

        // Send SMS
        try {
            await sendSMS(recipientPhone, message);
            notification.status = 'SENT';
            notification.sentAt = new Date();
        } catch (sendError) {
            notification.status = 'FAILED';
            notification.errorMessage = sendError.message;
        }

        await notification.save();

        res.status(201).json({
            message: `SMS notification ${notification.status.toLowerCase()}`,
            notification,
            recipient: recipientPhone,
            timestamp: new Date()
        });

    } catch (err) {
        next(err);
    }
};

// ─── DELETE NOTIFICATION ──────────────────────────────────────────────────
const deleteNotification = async (req, res, next) => {
    try {
        const notification = await Notification.findByIdAndDelete(req.params.id);
        if (!notification) {
            return res.status(404).json({
                error: `Notification not found with ID: ${req.params.id}`
            });
        }
        res.status(200).json({
            message: 'Notification deleted successfully',
            id: req.params.id
        });
    } catch (err) {
        next(err);
    }
};


module.exports = {
    getAllNotifications,
    getNotificationById,
    getNotificationsByGuest,
    getNotificationsByBooking,
    sendEmailNotification,
    sendSmsNotification,
    deleteNotification
};