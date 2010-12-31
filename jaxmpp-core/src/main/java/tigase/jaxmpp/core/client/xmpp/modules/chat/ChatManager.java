package tigase.jaxmpp.core.client.xmpp.modules.chat;

import java.util.ArrayList;
import java.util.List;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.PacketWriter;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.UIDGenerator;
import tigase.jaxmpp.core.client.observer.Observable;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.MessageModule;
import tigase.jaxmpp.core.client.xmpp.modules.MessageModule.MessageEvent;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;

public class ChatManager {

	private final ArrayList<Chat> chats = new ArrayList<Chat>();

	private final Observable observable;

	private final PacketWriter packetWriter;

	private final SessionObject sessionObject;

	public ChatManager(SessionObject sessionObject, PacketWriter packetWriter, Observable observable) {
		this.sessionObject = sessionObject;
		this.packetWriter = packetWriter;
		this.observable = observable;
	}

	public void close(Chat chat) {
		this.chats.remove(chat);
	}

	public Chat createChat(JID jid) {
		final String threadId = UIDGenerator.next();
		Chat chat = new Chat(packetWriter);
		chat.setThreadId(threadId);
		chat.setJid(jid);

		this.chats.add(chat);

		MessageEvent event = new MessageModule.MessageEvent(MessageModule.ChatCreated);
		event.setChat(chat);

		observable.fireEvent(event.getType(), event);

		return chat;
	}

	protected Chat getChat(JID jid, String threadId) {
		Chat chat = null;

		BareJID bareJID = jid.getBareJid();

		for (Chat c : this.chats) {
			if (!c.getJid().getBareJid().equals(bareJID)) {
				continue;
			}
			if (threadId != null && c.getThreadId() != null && threadId.equals(c.getThreadId())) {
				chat = c;
				break;
			}
			if (jid.getResource() != null && c.getJid().getResource() != null
					&& jid.getResource().equals(c.getJid().getResource())) {
				chat = c;
				break;
			}
			if (c.getJid().getResource() == null) {
				c.setJid(jid);
				chat = c;
				break;
			}

		}
		return chat;
	}

	public List<Chat> getChats() {
		return this.chats;
	}

	public Chat process(Message message) throws XMLException {
		if (message.getType() != StanzaType.chat)
			return null;
		final JID fromJid = message.getFrom();
		final String threadId = message.getThread();

		Chat chat = getChat(fromJid, threadId);

		if (chat == null) {
			chat = new Chat(packetWriter);
			chat.setJid(fromJid);
			chat.setThreadId(threadId);
			this.chats.add(chat);
			MessageEvent event = new MessageModule.MessageEvent(MessageModule.ChatCreated);
			event.setChat(chat);
			event.setMessage(message);

			observable.fireEvent(event.getType(), event);
		}

		return chat;
	}

}