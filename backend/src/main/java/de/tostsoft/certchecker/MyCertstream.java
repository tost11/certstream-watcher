package de.tostsoft.certchecker;

import com.google.gson.*;
import io.calidog.certstream.BoringParts;
import io.calidog.certstream.CertStreamMessage;
import io.calidog.certstream.CertStreamMessageHandler;
import io.calidog.certstream.CertStreamMessagePOJO;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.function.Consumer;


//only copied from certstream-java and edited some lines
public class MyCertstream{
    private static final Logger logger=LoggerFactory.getLogger(MyCertstream.class);

    private static final ExclusionStrategy strategy=new ExclusionStrategy(){
        @Override
        public boolean shouldSkipField(FieldAttributes field){
            if(field.getName().equals("extensions")){
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz){
            return false;
        }
    };

    public MyCertstream(){
    }

    public static void onMessageString(Consumer<String> handler){
        (new Thread(()->{
            handler.getClass();
            BoringParts theBoringParts=new BoringParts(handler::accept);
            try{
                HashSet<Integer> recoverableCloseCodes=(HashSet<Integer>)FieldUtils.readField(theBoringParts, "recoverableCloseCodes", true);
                recoverableCloseCodes.add(1000);
            }catch(IllegalAccessException ex){
                logger.error("Could not set reconnect codes", ex);
            }

            while(theBoringParts.isNotClosed()){
                Thread.yield();

                try{
                    Thread.sleep(1000L);
                }catch(InterruptedException var3){
                }

                if(Thread.interrupted()){
                    break;
                }
            }

        })).start();
    }

    public static void onMessage(CertStreamMessageHandler handler){
        //TODO make a pull request to fix these realy ugly fix
        Gson gson=new GsonBuilder().addDeserializationExclusionStrategy(strategy).create();

        onMessageString((string)->{
            CertStreamMessagePOJO msg;
            try{
                //System.out.println(string);
                msg=(CertStreamMessagePOJO)gson.fromJson(string, CertStreamMessagePOJO.class);
                String messageType=(String)FieldUtils.readField(msg, "messageType", true);
                if(messageType.equalsIgnoreCase("heartbeat")){
                    return;
                }
            }catch(IllegalAccessException|JsonSyntaxException var6){
                logger.warn("onMessage had an exception parsing some json", var6);
                return;
            }

            CertStreamMessage fullMsg;
            try{
                fullMsg=CertStreamMessage.fromPOJO(msg);
            }catch(CertificateException var5){
                logger.warn("Encountered a CertificateException", var5);
                return;
            }

            handler.onMessage(fullMsg);
        });
    }
}
