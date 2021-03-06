package skadistats.clarity.parser.handler;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skadistats.clarity.decoder.SendTableFlattener;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.ReceiveProp;
import skadistats.clarity.parser.Handler;
import skadistats.clarity.parser.HandlerHelper;
import skadistats.clarity.parser.RegisterHandler;

import com.dota2.proto.Demo.CDemoSyncTick;

@RegisterHandler(CDemoSyncTick.class)
public class DemSyncTickHandler implements Handler<CDemoSyncTick> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void apply(int peekTick, CDemoSyncTick message, Match match) {
        HandlerHelper.traceMessage(log, peekTick, message);
        
        // last packet of the prologue: compile receive tables!

        for (Iterator<DTClass> i = match.getDtClasses().iterator(); i.hasNext();) {
            DTClass dtc = i.next();
            if (!dtc.getSendTable().isDecoderNeeded()) {
                continue;
            }
            List<ReceiveProp> rps = new SendTableFlattener(match.getDtClasses(), dtc.getSendTable()).flatten();
            dtc.setReceiveProps(rps);
        }

        // last packet of the prologue: set super classes
        
        for (Iterator<DTClass> i = match.getDtClasses().iterator(); i.hasNext();) {
            DTClass dtc = i.next();
            String superClassName = dtc.getSendTable().getBaseClass();
            if (superClassName != null) {
                dtc.setSuperClass(match.getDtClasses().forDtName(superClassName));
            }
        }
        
    }

}
