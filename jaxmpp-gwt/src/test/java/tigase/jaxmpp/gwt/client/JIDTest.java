package tigase.jaxmpp.gwt.client;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;

import com.google.gwt.junit.client.GWTTestCase;

public class JIDTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "tigase.jaxmpp.gwt.JaxmppGWTJUnit";
	}

	public void testGetBareJid() {
		JID jid = JID.jidInstance("a@b/c");
		assertEquals(BareJID.bareJIDInstance("a", "b"), jid.getBareJid());
	}

	public void testGetDomain() {
		JID jid = JID.jidInstance("a@b/c");
		assertEquals("b", jid.getDomain());
	}

	public void testGetLocalpart() {
		JID jid = JID.jidInstance("a@b/c");
		assertEquals("a", jid.getLocalpart());
	}

	public void testGetResource() {
		JID jid = JID.jidInstance("a@b/c");
		assertEquals("c", jid.getResource());
	}

	public void testJIDInstance() {
		assertEquals(JID.jidInstance("a@b"), JID.jidInstance("a@b"));
		assertEquals(JID.jidInstance("a@b"), JID.jidInstance("a", "b"));
		assertEquals(JID.jidInstance("a", "b"), JID.jidInstance("a", "b"));
		assertEquals(JID.jidInstance("a@b/c"), JID.jidInstance("a@b/c"));
		assertEquals(JID.jidInstance("a", "b", "c"), JID.jidInstance("a@b/c"));

		assertFalse(JID.jidInstance("a@b").equals(JID.jidInstance("a@b/c")));
	}

	public void testToString() {
		JID jid = JID.jidInstance("a@b");
		assertEquals("a@b", jid.toString());

		jid = JID.jidInstance("a@b/c");
		assertEquals("a@b/c", jid.toString());
	}

}