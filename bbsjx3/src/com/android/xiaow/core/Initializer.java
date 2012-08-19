/**
 * @author:xiaowei
 * @version:2012-8-5����4:36:07
 */
package com.android.xiaow.core;

import com.android.xiaow.core.cmds.impl.ImageDownCommand;
import com.android.xiaow.core.filter.DefaultFilter;
import com.android.xiaow.core.threads.Manager;
import com.android.xiaow.core.threads.ThreadPoolConfig;
import com.android.xiaow.jx3bbs.cmds.BranchListCommand;
import com.android.xiaow.jx3bbs.cmds.CardCommand;
import com.android.xiaow.jx3bbs.cmds.InitCommand;
import com.android.xiaow.jx3bbs.cmds.InitMainListCommand;
import com.android.xiaow.jx3bbs.cmds.ReplayCommand;
import com.android.xiaow.jx3bbs.cmds.newThreadCommand;

/**
 * @author xiaowei
 * 
 */
public class Initializer {
    public static final int IMAGE_CMD_ID = 0x1001;
    public static final int INIT_CMD_ID = 0x1002;
    public static final int INIT_MAIN_BRACH_CMD_ID = 0x1003;
    public static final int BRANCH_LIST_CMD_ID = 0x1004;
    public static final int CARD_CMD_ID = 0x1005;
    public static final int REPLAY_CMD_ID = 0x1006;
    public static final int THREAD_CMD_ID = 0x1007;

    public static void ensureInitialized() {
        DefaultFilter imageFilter = new DefaultFilter(ThreadPoolConfig.MAX_IMAGE_DOWN);
        imageFilter.registerCommandId(IMAGE_CMD_ID, ImageDownCommand.class.getName());
        Manager.getIntance().addFilter(imageFilter);
        DefaultFilter filter = new DefaultFilter(ThreadPoolConfig.MAX_DEMON_THREAD_NUM);
        Manager.getIntance().addFilter(filter);
        filter.registerCommandId(INIT_CMD_ID, InitCommand.class.getName());
        filter.registerCommandId(INIT_MAIN_BRACH_CMD_ID, InitMainListCommand.class.getName());
        filter.registerCommandId(BRANCH_LIST_CMD_ID, BranchListCommand.class.getName());
        filter.registerCommandId(CARD_CMD_ID, CardCommand.class.getName());
        filter.registerCommandId(REPLAY_CMD_ID, ReplayCommand.class.getName());
        filter.registerCommandId(THREAD_CMD_ID, newThreadCommand.class.getName());
    }
}
