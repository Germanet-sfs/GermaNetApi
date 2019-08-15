package de.tuebingen.uni.sfs.germanet.semantic;

import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class Semantic {

    public static final int GNROOT_ID = 51001;
    private static final Logger LOGGER = LoggerFactory.getLogger(Semantic.class);
    private GermaNet gnet;
    private GermaNetGraphUtils germaNetGraphUtils;


    public Semantic(GermaNet gnet) {
        this.gnet = gnet;
        germaNetGraphUtils = new GermaNetGraphUtils(gnet);
    }
}
