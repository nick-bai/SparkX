package sparkx.service.helper;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.STRING;

@Component
@Slf4j
public class MemoryBuildHelper implements ChatMemoryStore {

    private final DB db = DBMaker.fileDB("multi-user-chat-memory.db").transactionEnable().make();
    private final Map<String, String> map = db.hashMap("messages", STRING, STRING).createOrOpen();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        String json = map.get(String.valueOf(memoryId));
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = messagesToJson(messages);
        map.put(String.valueOf(memoryId), json);
        db.commit();
    }

    @Override
    public void deleteMessages(Object memoryId) {
        map.remove(String.valueOf(memoryId));
        db.commit();
    }
}