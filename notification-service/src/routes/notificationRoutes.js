const express = require('express');
const router = express.Router();
const notificationController = require('../controllers/notificationController');

// GET all notifications
router.get('/', notificationController.getAllNotifications);

// GET notification by ID
router.get('/:id', notificationController.getNotificationById);

// GET notifications by guest ID
router.get('/guest/:guestId', notificationController.getNotificationsByGuest);

// GET notifications by booking ID
router.get('/booking/:bookingId', notificationController.getNotificationsByBooking);

// POST send email notification
router.post('/email', notificationController.sendEmailNotification);

// POST send SMS notification
router.post('/sms', notificationController.sendSmsNotification);

// DELETE notification
router.delete('/:id', notificationController.deleteNotification);

module.exports = router;