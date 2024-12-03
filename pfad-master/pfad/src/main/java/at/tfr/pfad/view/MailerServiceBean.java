package at.tfr.pfad.view;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Startup
@Singleton
public class MailerServiceBean implements Serializable {

    public static final int MAX_MESSAGES_PER_HOUR = 300;
    private final transient Map<String, MailQueueConfig> mailQueues = new HashMap<>();

    @Schedule(hour = "*", minute = "*", second = "*", persistent = false)
    public void timerTick() {
        mailQueues.values().forEach(mq -> mq.tick());
    }

    public int jiffies(String key) {
        if (mailQueues.containsKey(key)) {
            return mailQueues.get(key).getMailBucket();
        }
        return MAX_MESSAGES_PER_HOUR;
    }

    public int take(String key, int jiffies) {
        if (mailQueues.containsKey(key)) {
            return mailQueues.get(key).take(jiffies);
        }
        return MAX_MESSAGES_PER_HOUR;
    }

    public void setMailQueues(Map<String, MailerBean.MailConfig> mailConfigs) {
        mailConfigs.entrySet().forEach(mce -> {
            if (!mailQueues.containsKey(mce.getKey())) {
                mailQueues.put(mce.getKey(), new MailQueueConfig(mce.getValue()));
            }
        });
    }

    public static class MailQueueConfig {

        final MailerBean.MailConfig mailConfig;
        int mailBucket = MAX_MESSAGES_PER_HOUR;
        private long lastMillis = System.currentTimeMillis();

        MailQueueConfig(MailerBean.MailConfig config) {
            this.mailConfig = config;
        }

        public void tick() {
            long millis = System.currentTimeMillis();
            float diff = toJiffies(millis); // MAX_MESSAGES / Hour to increment
            if (diff > 1) {
                int intDiff = (int)Math.floor(diff);
                mailBucket += (int)diff;
                if (mailBucket > MAX_MESSAGES_PER_HOUR) mailBucket = MAX_MESSAGES_PER_HOUR;
                lastMillis = System.currentTimeMillis() - toMillis(diff - intDiff); // fractional part from int division
            }
        }

        private float toJiffies(long millis) {
            return Float.valueOf((millis - lastMillis) * MAX_MESSAGES_PER_HOUR) / (1000 * 3600);
        }

        private long toMillis(float jiffies) {
            return (long)Math.ceil(jiffies / MAX_MESSAGES_PER_HOUR * (1000 * 3600));
        }

        public int take(int jiffies) {
            mailBucket -= jiffies;
            return mailBucket;
        }

        public int getMailBucket() {
            return mailBucket;
        }
    }
}
