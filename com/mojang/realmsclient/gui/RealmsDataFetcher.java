package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.client.RealmsClient;
import org.apache.logging.log4j.LogManager;
import java.util.Comparator;
import java.util.Collections;
import net.minecraft.realms.Realms;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import java.util.List;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.logging.log4j.Logger;

public class RealmsDataFetcher {
    private static final Logger LOGGER;
    private final ScheduledExecutorService scheduler;
    private volatile boolean stopped;
    private final ServerListUpdateTask serverListUpdateTask;
    private final PendingInviteUpdateTask pendingInviteUpdateTask;
    private final TrialAvailabilityTask trialAvailabilityTask;
    private final LiveStatsTask liveStatsTask;
    private final UnreadNewsTask unreadNewsTask;
    private final Set<RealmsServer> removedServers;
    private List<RealmsServer> servers;
    private RealmsServerPlayerLists livestats;
    private int pendingInvitesCount;
    private boolean trialAvailable;
    private boolean hasUnreadNews;
    private String newsLink;
    private ScheduledFuture<?> serverListScheduledFuture;
    private ScheduledFuture<?> pendingInviteScheduledFuture;
    private ScheduledFuture<?> trialAvailableScheduledFuture;
    private ScheduledFuture<?> liveStatsScheduledFuture;
    private ScheduledFuture<?> unreadNewsScheduledFuture;
    private final Map<Task, Boolean> fetchStatus;
    
    public RealmsDataFetcher() {
        this.scheduler = Executors.newScheduledThreadPool(3);
        this.stopped = true;
        this.serverListUpdateTask = new ServerListUpdateTask();
        this.pendingInviteUpdateTask = new PendingInviteUpdateTask();
        this.trialAvailabilityTask = new TrialAvailabilityTask();
        this.liveStatsTask = new LiveStatsTask();
        this.unreadNewsTask = new UnreadNewsTask();
        this.removedServers = (Set<RealmsServer>)Sets.newHashSet();
        this.servers = (List<RealmsServer>)Lists.newArrayList();
        this.fetchStatus = (Map<Task, Boolean>)new ConcurrentHashMap(Task.values().length);
    }
    
    public boolean isStopped() {
        return this.stopped;
    }
    
    public synchronized void init() {
        if (this.stopped) {
            this.stopped = false;
            this.cancelTasks();
            this.scheduleTasks();
        }
    }
    
    public synchronized void initWithSpecificTaskList(final List<Task> list) {
        if (this.stopped) {
            this.stopped = false;
            this.cancelTasks();
            for (final Task d4 : list) {
                this.fetchStatus.put(d4, false);
                switch (d4) {
                    case SERVER_LIST: {
                        this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
                        continue;
                    }
                    case PENDING_INVITE: {
                        this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
                        continue;
                    }
                    case TRIAL_AVAILABLE: {
                        this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
                        continue;
                    }
                    case LIVE_STATS: {
                        this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
                        continue;
                    }
                    case UNREAD_NEWS: {
                        this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
                        continue;
                    }
                }
            }
        }
    }
    
    public boolean isFetchedSinceLastTry(final Task d) {
        final Boolean boolean3 = (Boolean)this.fetchStatus.get(d);
        return boolean3 != null && boolean3;
    }
    
    public void markClean() {
        for (final Task d3 : this.fetchStatus.keySet()) {
            this.fetchStatus.put(d3, false);
        }
    }
    
    public synchronized void forceUpdate() {
        this.stop();
        this.init();
    }
    
    public synchronized List<RealmsServer> getServers() {
        return (List<RealmsServer>)Lists.newArrayList((Iterable)this.servers);
    }
    
    public synchronized int getPendingInvitesCount() {
        return this.pendingInvitesCount;
    }
    
    public synchronized boolean isTrialAvailable() {
        return this.trialAvailable;
    }
    
    public synchronized RealmsServerPlayerLists getLivestats() {
        return this.livestats;
    }
    
    public synchronized boolean hasUnreadNews() {
        return this.hasUnreadNews;
    }
    
    public synchronized String newsLink() {
        return this.newsLink;
    }
    
    public synchronized void stop() {
        this.stopped = true;
        this.cancelTasks();
    }
    
    private void scheduleTasks() {
        for (final Task d5 : Task.values()) {
            this.fetchStatus.put(d5, false);
        }
        this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
        this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
        this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
        this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
        this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate((Runnable)this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
    }
    
    private void cancelTasks() {
        try {
            if (this.serverListScheduledFuture != null) {
                this.serverListScheduledFuture.cancel(false);
            }
            if (this.pendingInviteScheduledFuture != null) {
                this.pendingInviteScheduledFuture.cancel(false);
            }
            if (this.trialAvailableScheduledFuture != null) {
                this.trialAvailableScheduledFuture.cancel(false);
            }
            if (this.liveStatsScheduledFuture != null) {
                this.liveStatsScheduledFuture.cancel(false);
            }
            if (this.unreadNewsScheduledFuture != null) {
                this.unreadNewsScheduledFuture.cancel(false);
            }
        }
        catch (Exception exception2) {
            RealmsDataFetcher.LOGGER.error("Failed to cancel Realms tasks", (Throwable)exception2);
        }
    }
    
    private synchronized void setServers(final List<RealmsServer> list) {
        int integer3 = 0;
        for (final RealmsServer realmsServer5 : this.removedServers) {
            if (list.remove(realmsServer5)) {
                ++integer3;
            }
        }
        if (integer3 == 0) {
            this.removedServers.clear();
        }
        this.servers = list;
    }
    
    public synchronized void removeItem(final RealmsServer realmsServer) {
        this.servers.remove(realmsServer);
        this.removedServers.add(realmsServer);
    }
    
    private void sort(final List<RealmsServer> list) {
        Collections.sort((List)list, (Comparator)new RealmsServer.McoServerComparator(Realms.getName()));
    }
    
    private boolean isActive() {
        return !this.stopped;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    class ServerListUpdateTask implements Runnable {
        private ServerListUpdateTask() {
        }
        
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updateServersList();
            }
        }
        
        private void updateServersList() {
            try {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                if (cvm2 != null) {
                    final List<RealmsServer> list3 = cvm2.listWorlds().servers;
                    if (list3 != null) {
                        RealmsDataFetcher.this.sort(list3);
                        RealmsDataFetcher.this.setServers(list3);
                        RealmsDataFetcher.this.fetchStatus.put(Task.SERVER_LIST, true);
                    }
                    else {
                        RealmsDataFetcher.LOGGER.warn("Realms server list was null or empty");
                    }
                }
            }
            catch (Exception exception2) {
                RealmsDataFetcher.this.fetchStatus.put(Task.SERVER_LIST, true);
                RealmsDataFetcher.LOGGER.error("Couldn't get server list", (Throwable)exception2);
            }
        }
    }
    
    class PendingInviteUpdateTask implements Runnable {
        private PendingInviteUpdateTask() {
        }
        
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updatePendingInvites();
            }
        }
        
        private void updatePendingInvites() {
            try {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                if (cvm2 != null) {
                    RealmsDataFetcher.this.pendingInvitesCount = cvm2.pendingInvitesCount();
                    RealmsDataFetcher.this.fetchStatus.put(Task.PENDING_INVITE, true);
                }
            }
            catch (Exception exception2) {
                RealmsDataFetcher.LOGGER.error("Couldn't get pending invite count", (Throwable)exception2);
            }
        }
    }
    
    class TrialAvailabilityTask implements Runnable {
        private TrialAvailabilityTask() {
        }
        
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getTrialAvailable();
            }
        }
        
        private void getTrialAvailable() {
            try {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                if (cvm2 != null) {
                    RealmsDataFetcher.this.trialAvailable = cvm2.trialAvailable();
                    RealmsDataFetcher.this.fetchStatus.put(Task.TRIAL_AVAILABLE, true);
                }
            }
            catch (Exception exception2) {
                RealmsDataFetcher.LOGGER.error("Couldn't get trial availability", (Throwable)exception2);
            }
        }
    }
    
    class LiveStatsTask implements Runnable {
        private LiveStatsTask() {
        }
        
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getLiveStats();
            }
        }
        
        private void getLiveStats() {
            try {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                if (cvm2 != null) {
                    RealmsDataFetcher.this.livestats = cvm2.getLiveStats();
                    RealmsDataFetcher.this.fetchStatus.put(Task.LIVE_STATS, true);
                }
            }
            catch (Exception exception2) {
                RealmsDataFetcher.LOGGER.error("Couldn't get live stats", (Throwable)exception2);
            }
        }
    }
    
    class UnreadNewsTask implements Runnable {
        private UnreadNewsTask() {
        }
        
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getUnreadNews();
            }
        }
        
        private void getUnreadNews() {
            try {
                final RealmsClient cvm2 = RealmsClient.createRealmsClient();
                if (cvm2 != null) {
                    RealmsNews realmsNews3 = null;
                    try {
                        realmsNews3 = cvm2.getNews();
                    }
                    catch (Exception ex) {}
                    final RealmsPersistence.RealmsPersistenceData a4 = RealmsPersistence.readFile();
                    if (realmsNews3 != null) {
                        final String string5 = realmsNews3.newsLink;
                        if (string5 != null && !string5.equals(a4.newsLink)) {
                            a4.hasUnreadNews = true;
                            a4.newsLink = string5;
                            RealmsPersistence.writeFile(a4);
                        }
                    }
                    RealmsDataFetcher.this.hasUnreadNews = a4.hasUnreadNews;
                    RealmsDataFetcher.this.newsLink = a4.newsLink;
                    RealmsDataFetcher.this.fetchStatus.put(Task.UNREAD_NEWS, true);
                }
            }
            catch (Exception exception2) {
                RealmsDataFetcher.LOGGER.error("Couldn't get unread news", (Throwable)exception2);
            }
        }
    }
    
    public enum Task {
        SERVER_LIST, 
        PENDING_INVITE, 
        TRIAL_AVAILABLE, 
        LIVE_STATS, 
        UNREAD_NEWS;
    }
}
