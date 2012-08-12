/**   
 * @Title: AbstractCommand.java 
 * @Package com.yhiker.playmate.core.cmds 
 * @Description: TODO
 * @author xiaowei   
 * @date 2012-7-23 ����4:33:33 
 * @version V1.0   
 */
package com.android.xiaow.core.cmds;

import com.android.xiaow.core.common.CommandStatus;
import com.android.xiaow.core.common.ObserverInfo;

/**
 * 
 * @author ���� xiaowei
 * @����ʱ�� 2012-7-23 ����4:33:33 ��˵��
 * 
 */
public abstract class AbstractCommand extends BaseCommand {

    @Override
    public void execute() {
        notifyStart();
        prepare();
        onBeforeExecute();
        go();
        onAfterExecute();
        if (listener != null) {
            if (getResponse().isError) {
                listener.onError(getResponse());
            } else {
                listener.onSuccess(getResponse());
            }
        }
        notifyStop();
    }

    public void notifyStart() {

        ObserverInfo info = new ObserverInfo();
        info.status = CommandStatus.start;
        getObservable().notifyObservers(info);
    }

    public void notifyStop() {
        ObserverInfo info = new ObserverInfo();
        info.status = CommandStatus.finish;
        getObservable().notifyObservers(info);
    }

    public void prepare() {
    }

    public void onBeforeExecute() {
    }

    public abstract void go();

    public void onAfterExecute() {
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public void onTerminal() {
    }

}
