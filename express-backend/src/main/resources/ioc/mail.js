/**
 *@description: mail邮件配置
 */
var ioc={
    emailAuthenticator : {
        type : "org.apache.commons.mail.DefaultAuthenticator",
        args : [{java:"$conf.get('mail.UserName')"}, {java:"$conf.get('mail.Password')"}]
    },
    htmlEmail : {
        type : "org.apache.commons.mail.ImageHtmlEmail",
        singleton : false,
        fields : {
            hostName : {java:"$conf.get('mail.HostName')"},
            smtpPort : {java:"$conf.get('mail.SmtpPort')"},
            authenticator : {refer:"emailAuthenticator"},
            SSLOnConnect : {java:"$conf.get('mail.SSLOnConnect')"},
            from : {java:"$conf.get('mail.From')"},
            charset : {java:"$conf.get('mail.charset', 'UTF-8')"}
        }
    }
};