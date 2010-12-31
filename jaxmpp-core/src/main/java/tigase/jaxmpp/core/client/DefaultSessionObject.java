package tigase.jaxmpp.core.client;

import java.util.HashMap;
import java.util.Map;

import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceStore;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterStore;

public class DefaultSessionObject implements SessionObject {

	private final PresenceStore presence = new PresenceStore();

	protected final Map<String, Object> properties = new HashMap<String, Object>();

	protected final ResponseManager responseManager = new ResponseManager();

	private final RosterStore roster = new RosterStore();

	protected Element streamFeatures;

	protected final Map<String, Object> userProperties = new HashMap<String, Object>();

	@Override
	public void clear() {
		this.properties.clear();
	}

	@Override
	public PresenceStore getPresence() {
		return presence;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key) {
		T t = (T) this.userProperties.get(key);
		if (t == null)
			t = (T) this.properties.get(key);
		return t;
	}

	@Override
	public Runnable getResponseHandler(Element element, PacketWriter writer, SessionObject sessionObject) throws XMLException {
		return responseManager.getResponseHandler(element, writer, sessionObject);
	}

	@Override
	public RosterStore getRoster() {
		return roster;
	}

	@Override
	public Element getStreamFeatures() {
		return this.streamFeatures;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserProperty(String key) {
		return (T) this.userProperties.get(key);
	}

	@Override
	public String registerResponseHandler(Element stanza, AsyncCallback callback) throws XMLException {
		return responseManager.registerResponseHandler(stanza, callback);
	}

	@Override
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}

	@Override
	public void setStreamFeatures(Element element) {
		this.streamFeatures = element;
	}

	@Override
	public void setUserProperty(String key, Object value) {
		if (value == null)
			this.userProperties.remove(key);
		this.userProperties.put(key, value);
	}

}