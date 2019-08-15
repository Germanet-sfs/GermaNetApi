package de.tuebingen.uni.sfs.germanet.graph;

import java.util.List;

/**
 * author: meh, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public interface GermaNetVertex {
    int getId();
    List<String> getOrthForms();
    String getLabel();
}
