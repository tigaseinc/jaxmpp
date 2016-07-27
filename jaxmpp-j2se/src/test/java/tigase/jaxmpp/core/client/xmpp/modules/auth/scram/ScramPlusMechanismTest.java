package tigase.jaxmpp.core.client.xmpp.modules.auth.scram;

import org.junit.Assert;
import org.junit.Test;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.Base64;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.j2se.J2SESessionObject;
import tigase.jaxmpp.j2se.connectors.socket.SocketConnector;

public class ScramPlusMechanismTest {

	@Test
	public void testMessages() throws Exception {
		SessionObject sessionObject = new J2SESessionObject();
		sessionObject.setProperty(SessionObject.USER_BARE_JID, BareJID.bareJIDInstance("bmalkow@example.com"));
		sessionObject.setProperty(SessionObject.PASSWORD, "123456");
		sessionObject.setProperty(SocketConnector.TLS_SESSION_ID_KEY, new byte[]{'D', 'P', 'I'});

		ScramPlusMechanism scram = new ScramPlusMechanism() {
			@Override
			protected String randomString() {
				return "SpiXKmhi57DBp5sdE5G3H3ms";
			}
		};

		String firstClientMessage = new String(Base64.decode(scram.evaluateChallenge(null, sessionObject)));
		Assert.assertEquals("p=tls-unique,,n=bmalkow,r=SpiXKmhi57DBp5sdE5G3H3ms", firstClientMessage);

		String serverFirstMessage = "r=SpiXKmhi57DBp5sdE5G3H3ms5kLrhitKUHVoSOmzdR,s=Ey6OJnGx7JEJAIJp,i=4096";
		String clientLastMessage = new String(Base64.decode(scram.evaluateChallenge(Base64.encode(serverFirstMessage.getBytes()), sessionObject)));
		Assert.assertEquals("c=cD10bHMtdW5pcXVlLCxEUEk=,r=SpiXKmhi57DBp5sdE5G3H3ms5kLrhitKUHVoSOmzdR,p=+zQvUd4nQqo03thSCcc2K6gueD4=", clientLastMessage);

		Assert.assertFalse(scram.isComplete(sessionObject));

		String serverLastMessage = "v=NQ/f8FjeMxUuRK9F88G8tMji4pk=";
		Assert.assertNull(scram.evaluateChallenge(Base64.encode(serverLastMessage.getBytes()), sessionObject));

		Assert.assertTrue(scram.isComplete(sessionObject));
	}

}