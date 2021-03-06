'use strict';

var kafka = require('kafka-node');
var nodeMailer = require('nodemailer');
var handlebars = require('handlebars');
var fs = require('fs');

const PROPOSALS_REVIEWED_TOPIC = 'proposals-reviewed';
const MAILCATCHER_SMTP_HOST = 'localhost';
const MAILCATCHER_SMTP_PORT = 1025;
const EMAIL_FROM = 'proposals@myconference.com';
const ACCEPTED_PROPOSAL_HB_TEMPLATE_FILE_NAME =
  './templates/acceptedProposal.hbs';

const REJECTED_PROPOSAL_HB_TEMPLATE_FILE_NAME =
  './templates/rejectedProposal.hbs';

const UTF_8 = 'utf8';

var consumer = new kafka.ConsumerGroup({
  fromOffset: 'latest',
  autoCommit: true
}, PROPOSALS_REVIEWED_TOPIC);

var mailCatcherSmtpConfig = {
  host: MAILCATCHER_SMTP_HOST,
  port: MAILCATCHER_SMTP_PORT,
};

var transporter = nodeMailer.createTransport(mailCatcherSmtpConfig);

consumer.on('message', function(message) {
  // console.log('received message', message);
  notifySpeaker(message.value);
});

consumer.on('error', function(err) {
  console.log(err);
});

process.on('SIGINT', function() {
  console.log(
    'SIGINT received - Proposal Reviewer closing. Committing current offset on Topic: ' +
    PROPOSALS_REVIEWED_TOPIC + ' ...'
  );

  consumer.close(true, function() {
    console.log(
      'Finished committing current offset. Exiting with graceful shutdown ...'
    );

    process.exit();
  });
});

function notifySpeaker(notification) {
  var notificationMessage = createNotificationMessage(notification);

  sendEmail(notificationMessage);
}

function createNotificationMessage(notification) {
  var notificationAsObj = JSON.parse(notification);
  var proposal = notificationAsObj.proposal;

  console.log('Notification Message = ' + notification);

  var mailOptions = {
    from: EMAIL_FROM, // sender address 
    to: proposal.speaker.email, // list of receivers 
    subject: proposal.conference.name + ' - ' + proposal.session.title, // Subject line 
    html: createEmailBody(notificationAsObj)
  };

  return mailOptions;
}

function createEmailBody(notification) {
  // Read Handlebars Template file.
  var hbTemplateContent = fs.readFileSync(((notification.decision.accepted) ?
    ACCEPTED_PROPOSAL_HB_TEMPLATE_FILE_NAME :
    REJECTED_PROPOSAL_HB_TEMPLATE_FILE_NAME), UTF_8);

  // Compile the template into a function.
  var template = handlebars.compile(hbTemplateContent);
  var body = template(notification); // Render the template.

  console.log('Email body = ' + body);
  return body;
}

function sendEmail(mailOptions) {
  // send mail with defined transport object 
  transporter.sendMail(mailOptions, function(error, info) {
    if (error) {
      console.log(error);
    } else {
      console.log('Email Message sent: ' + info.response);
    }
  });
}
