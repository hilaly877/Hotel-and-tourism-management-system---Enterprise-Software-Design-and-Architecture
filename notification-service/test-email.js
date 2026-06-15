require("dotenv").config();
const nodemailer = require("nodemailer");

console.log("🔍 Testing Email Configuration...\n");

console.log("Configuration values:");
console.log(`  EMAIL_USER: ${process.env.EMAIL_USER}`);
console.log(
  `  EMAIL_PASS: ${process.env.EMAIL_PASS ? "***" + process.env.EMAIL_PASS.slice(-4) : "NOT SET"}`,
);
console.log("");

async function test() {
  const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
      user: process.env.EMAIL_USER,
      pass: process.env.EMAIL_PASS,
    },
    logger: true,
    debug: true,
  });

  try {
    console.log("📧 Verifying SMTP connection...\n");

    await transporter.verify();
    console.log("✅ SMTP VERIFIED SUCCESSFULLY");

    console.log("\n📬 Sending test email...\n");

    const info = await transporter.sendMail({
      from: `"Hotel System" <${process.env.EMAIL_USER}>`,
      to: process.env.EMAIL_USER,
      subject: "Test Email from System",
      text: "Email is working!",
      html: "<h2>Success</h2><p>Email system is working correctly.</p>",
    });

    console.log("✅ EMAIL SENT!");
    console.log("Message ID:", info.messageId);
    console.log("Accepted:", info.accepted);
    console.log("Rejected:", info.rejected);
    console.log("Message ID:", info.messageId);
  } catch (error) {
    console.error("❌ ERROR:", error.message);
  }
}

test();
