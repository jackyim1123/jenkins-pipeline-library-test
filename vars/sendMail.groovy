// vars/sendMail.groovy
/**
 * sendMail.groovy
 *
 * This function will use emailext to send an email
 * - An email will always be send to the requestor (the one who started the job)
 * - In case of a failure the email will be send to the curlprits
 *
 * @param emailRecipients	[null|string]												The email recipients
 * @param subject			["<JOB_NAME> - Build #<BUILD_NUMBER> - <result>!" | string] The email subject
 * @param attachLog			[null|true|false]											By default the buildlog will be included when the build fails
 *
 */

def call(body) {
	// evaluate the body block, and collect configuration into the object
	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = config
	body()

	def to = config.emailRecipients
	def subject = config.subject ? config.subject : "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.result}!"
	def content = '${JELLY_SCRIPT,template="static-analysis"}'
	def attachLog = (config.attachLog != null) ? config.attachLog : (currentBuild.result != "SUCCESS") // Attach buildlog when the build is not successfull
	def recipientProviders = "[[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']]"

	// Send email
     emailext(body: content, mimeType: 'text/html',
         replyTo: '$DEFAULT_REPLYTO', subject: subject,
         to: to, attachLog: attachLog, recipientProviders: recipientProviders)
}