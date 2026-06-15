const nodemailer = require('nodemailer');

console.log('📧 Initializing Nodemailer SMTP client...');
console.log(`   Email: ${process.env.EMAIL_USER}`);
console.log(`   Host: ${process.env.EMAIL_HOST}`);
console.log(`   Port: ${process.env.EMAIL_PORT}`);

// Create SMTP transporter
const port = parseInt(process.env.EMAIL_PORT, 10) || 587;
const secure = port === 465; // use implicit TLS for port 465
console.log(`   Using secure connection: ${secure}`);
const transporter = nodemailer.createTransport({
    host: process.env.EMAIL_HOST,
    port: port,
    secure: secure,
    requireTLS: !secure,
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    },
    logger: false,
    debug: false,
    tls: {
        rejectUnauthorized: false
    }
});

// Verify connection
transporter.verify((error, success) => {
    if (error) {
        console.error('❌ Email client FAILED to initialize:');
        console.error('   Error:', error.message);
        console.error('');
        console.error('Troubleshooting:');
        console.error('1. Check EMAIL_USER in .env (should be Gmail)');
        console.error('2. Check EMAIL_PASS in .env (16-char app password)');
        console.error('3. Ensure 2-Step Verification is ON in Gmail');
        console.error('4. Go to: https://myaccount.google.com/apppasswords');
        console.error('');
        console.error('Emails will be SIMULATED only');
    } else {
        console.log('✅ Email client initialized successfully');
        console.log('✅ Ready to send REAL emails via SMTP');
    }
});

module.exports = transporter;