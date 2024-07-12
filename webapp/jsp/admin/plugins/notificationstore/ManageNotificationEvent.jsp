<jsp:useBean id="manageNotificationEvent" scope="session" class="fr.paris.lutece.plugins.notificationstore.web.NotificationEventJspBean" />
<% String strContent = manageNotificationEvent.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
