package edu.bu.met.cs633.utils

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail

fun sendEmail(
    firstName: String,
    lastName: String,
    appointmentTime: String,
    toEmail: String,
    vehicle: String,
    phoneNumber: String,
    services: String,
) {
    println("Sending Email!")

    val senderEmail = "alan.servicelane@gmail.com"
    val password = "vsiifhdqpzvfglab"
    val smtpPort = 587

    val email = HtmlEmail()
    email.hostName = "smtp.gmail.com"
    email.setSmtpPort(smtpPort)
    email.setAuthenticator(DefaultAuthenticator(senderEmail, password))
    email.isStartTLSEnabled = true
    email.isSSLOnConnect = false
    email.setFrom(senderEmail)
    email.addTo(toEmail)
    email.subject = "ServiceLane Appointment Confirmation"
//    val kotlinLogoURL = URL("https://kotlinlang.org/docs/images/kotlin-logo.png")
//    val cid = email.embed(kotlinLogoURL, "Kotlin logo")
//    email.setHtmlMsg("<html><h1>Kotlin logo</h1><img src=\"cid:$cid\"></html>")

    // Email template based upon: https://github.com/sendgrid/email-templates
    email.setHtmlMsg(
        "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "\n" +
            "  <meta charset=\"utf-8\">\n" +
            "  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" +
            "  <title>ServiceLane Appointment Receipt</title>\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "  <style type=\"text/css\">\n" +
            "    /**\n" +
            "     * Google webfonts. Recommended to include the .woff version for cross-client compatibility.\n" +
            "     */\n" +
            "    @media screen {\n" +
            "      @font-face {\n" +
            "        font-family: 'Source Sans Pro';\n" +
            "        font-style: normal;\n" +
            "        font-weight: 400;\n" +
            "        src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\n" +
            "      }\n" +
            "\n" +
            "      @font-face {\n" +
            "        font-family: 'Source Sans Pro';\n" +
            "        font-style: normal;\n" +
            "        font-weight: 700;\n" +
            "        src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\n" +
            "      }\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Avoid browser level font resizing.\n" +
            "     * 1. Windows Mobile\n" +
            "     * 2. iOS / OSX\n" +
            "     */\n" +
            "    body,\n" +
            "    table,\n" +
            "    td,\n" +
            "    a {\n" +
            "      -ms-text-size-adjust: 100%; /* 1 */\n" +
            "      -webkit-text-size-adjust: 100%; /* 2 */\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Remove extra space added to tables and cells in Outlook.\n" +
            "     */\n" +
            "    table,\n" +
            "    td {\n" +
            "      mso-table-rspace: 0pt;\n" +
            "      mso-table-lspace: 0pt;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Better fluid images in Internet Explorer.\n" +
            "     */\n" +
            "    img {\n" +
            "      -ms-interpolation-mode: bicubic;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Remove blue links for iOS devices.\n" +
            "     */\n" +
            "    a[x-apple-data-detectors] {\n" +
            "      font-family: inherit !important;\n" +
            "      font-size: inherit !important;\n" +
            "      font-weight: inherit !important;\n" +
            "      line-height: inherit !important;\n" +
            "      color: inherit !important;\n" +
            "      text-decoration: none !important;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Fix centering issues in Android 4.4.\n" +
            "     */\n" +
            "    div[style*=\"margin: 16px 0;\"] {\n" +
            "      margin: 0 !important;\n" +
            "    }\n" +
            "\n" +
            "    body {\n" +
            "      width: 100% !important;\n" +
            "      height: 100% !important;\n" +
            "      padding: 0 !important;\n" +
            "      margin: 0 !important;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Collapse table borders to avoid space between cells.\n" +
            "     */\n" +
            "    table {\n" +
            "      border-collapse: collapse !important;\n" +
            "    }\n" +
            "\n" +
            "    a {\n" +
            "      color: #1a82e2;\n" +
            "    }\n" +
            "\n" +
            "    img {\n" +
            "      height: auto;\n" +
            "      line-height: 100%;\n" +
            "      text-decoration: none;\n" +
            "      border: 0;\n" +
            "      outline: none;\n" +
            "    }\n" +
            "\n" +
            "    .border {\n" +
            "      border: 5px solid black;\n" +
            "    }\n" +
            "  </style>\n" +
            "\n" +
            "</head>\n" +
            "<body style=\"background-color: #3f51b5;\">\n" +
            "\n" +
            "<!-- start preheader -->\n" +
            "<div class=\"preheader\" style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\n" +
            "  A preheader is the short summary text that follows the subject line when an email is viewed in the inbox.\n" +
            "</div>\n" +
            "<!-- end preheader -->\n" +
            "\n" +
            "<!-- start body -->\n" +
            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
            "\n" +
            "  <!-- start logo -->\n" +
            "  <tr>\n" +
            "    <td align=\"center\" bgcolor=\"#3f51b5\">\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
            "        <tr>\n" +
            "          <td align=\"center\" valign=\"top\" width=\"600\">\n" +
            "      <![endif]-->\n" +
            "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
            "        <tr>\n" +
            "          <td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\">\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "      </table>\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      </td>\n" +
            "      </tr>\n" +
            "      </table>\n" +
            "      <![endif]-->\n" +
            "    </td>\n" +
            "  </tr>\n" +
            "  <!-- end logo -->\n" +
            "\n" +
            "  <!-- start hero -->\n" +
            "  <tr>\n" +
            "    <td align=\"center\" bgcolor=\"#3f51b5\">\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
            "        <tr>\n" +
            "          <td align=\"center\" valign=\"top\" width=\"600\">\n" +
            "      <![endif]-->\n" +
            "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
            "        <tr>\n" +
            "          <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n" +
            "            <h1 style=\"margin: 0; font-size: 22px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">Appointment Confirmation: Servicelane</h1>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "      </table>\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      </td>\n" +
            "      </tr>\n" +
            "      </table>\n" +
            "      <![endif]-->\n" +
            "    </td>\n" +
            "  </tr>\n" +
            "  <!-- end hero -->\n" +
            "\n" +
            "  <!-- start copy block -->\n" +
            "  <tr>\n" +
            "    <td align=\"center\" bgcolor=\"#3f51b5\">\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
            "        <tr>\n" +
            "          <td align=\"center\" valign=\"top\" width=\"600\">\n" +
            "      <![endif]-->\n" +
            "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
            "\n" +
            "        <!-- start copy -->\n" +
            "        <tr>\n" +
            "          <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
            "            <p style=\"margin: 0;\">$firstName,<br>Your appointment has been confirmed! </p>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "        <!-- end copy -->\n" +
            "\n" +
            "        <!-- start receipt table -->\n" +
            "        <tr>\n" +
            "          <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
            "            <table class=\"border\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
            "              <tr>\n" +
            "                <td align=\"left\" width=\"75%\" style=\"padding: 6px 12px;font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">$appointmentTime</td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td align=\"left\" width=\"75%\" style=\"padding: 6px 12px;font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
            "                  <p>ServiceLane<br>147 West Street, Boston, MA 02136<br>555-444-3333</p>\n" +
            "                </td>\n" +
            "              </tr>\n" +
            "            </table>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "        <!-- end receipt table -->\n" +
            "\n" +
            "      </table>\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      </td>\n" +
            "      </tr>\n" +
            "      </table>\n" +
            "      <![endif]-->\n" +
            "    </td>\n" +
            "  </tr>\n" +
            "  <!-- end copy block -->\n" +
            "\n" +
            "  <!-- start receipt address block -->\n" +
            "  <tr>\n" +
            "    <td align=\"center\" bgcolor=\"#3f51b5\">\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
            "        <tr>\n" +
            "          <td align=\"center\" valign=\"top\" width=\"600\">\n" +
            "      <![endif]-->\n" +
            "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
            "        <!-- start receipt table -->\n" +
            "        <tr>\n" +
            "          <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
            "            <table class=\"border\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
            "              <tr>\n" +
            "                <td align=\"left\" width=\"75%\" style=\"padding: 6px 12px;font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
            "                  Customer Information\n" +
            "                </td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td align=\"left\" width=\"75%\" style=\"padding: 6px 12px;font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
            "                  <p>$firstName $lastName<br>$phoneNumber<br>$vehicle</p>\n" +
            "                </td>\n" +
            "              </tr>\n" +
            "              <tr>\n" +
            "                <td align=\"left\" width=\"75%\" style=\"padding: 6px 12px;font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
            "                  <p>Details<br>$services</p>\n" +
            "                </td>\n" +
            "              </tr>\n" +
            "            </table>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "        <!-- end receipt table -->\n" +
            "\n" +
            "      </table>\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      </td>\n" +
            "      </tr>\n" +
            "      </table>\n" +
            "      <![endif]-->\n" +
            "    </td>\n" +
            "  </tr>\n" +
            "  <!-- end receipt address block -->\n" +
            "\n" +
            "  <!-- start footer -->\n" +
            "  <tr>\n" +
            "    <td align=\"center\" bgcolor=\"#3f51b5\" style=\"padding: 24px;\">\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
            "        <tr>\n" +
            "          <td align=\"center\" valign=\"top\" width=\"600\">\n" +
            "      <![endif]-->\n" +
            "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
            "\n" +
            "        <!-- start permission -->\n" +
            "        <tr>\n" +
            "          <td align=\"center\" bgcolor=\"#3f51b5\" style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #000000;\">\n" +
            "            <p style=\"margin: 0;\"></p>\n" +
            "          </td>\n" +
            "        </tr>\n" +
            "        <!-- end permission -->\n" +
            "\n" +
            "      </table>\n" +
            "      <!--[if (gte mso 9)|(IE)]>\n" +
            "      </td>\n" +
            "      </tr>\n" +
            "      </table>\n" +
            "      <![endif]-->\n" +
            "    </td>\n" +
            "  </tr>\n" +
            "  <!-- end footer -->\n" +
            "\n" +
            "</table>\n" +
            "<!-- end body -->\n" +
            "</body>\n" +
            "</html>\n"
    )

    email.send()
}
