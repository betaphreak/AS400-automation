package ro.nn.qa.automation.terminal;

import org.tn5250j.SessionPanel;
import org.tn5250j.event.SessionChangeEvent;
import org.tn5250j.event.SessionJumpEvent;
import org.tn5250j.gui.GenericTn5250JFrame;

/**
 * Created by fd09lt on 01.09.2015.
 */
public abstract class TerminalViewInterface extends GenericTn5250JFrame
{
        private static final long serialVersionUID = 1L;
        protected static Terminal me;
        protected static int sequence;
        protected int frameSeq;

        public TerminalViewInterface(Terminal var1)
        {
            me = var1;
        }

        public int getFrameSequence()
        {
            return this.frameSeq;
        }

        public abstract void addSessionView(String var1, SessionPanel var2);

        public abstract void removeSessionView(SessionPanel var1);

        public abstract boolean containsSession(SessionPanel var1);

        public abstract int getSessionViewCount();

        public abstract SessionPanel getSessionAt(int var1);

        public abstract void onSessionJump(SessionJumpEvent var1);

        public abstract void onSessionChanged(SessionChangeEvent var1);
}
