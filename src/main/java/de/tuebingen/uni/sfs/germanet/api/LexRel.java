/*
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
 *
 * This file is part of the Java API to GermaNet.
 *
 * The Java API to GermaNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java API to GermaNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this API; if not, see <http://www.gnu.org/licenses/>.
 */
package de.tuebingen.uni.sfs.germanet.api;

/**
 * Enumeration of all lexical relations.
 * 
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
public enum LexRel {
    has_synonym,
    has_antonym,
    has_pertainym,
    has_participle,
    has_active_usage,
    has_occasion,
    has_attribute,
    has_appearance,
    has_construction_method,
    has_container,
    is_container_for,
    has_consistency_of,
    has_component,
    has_owner,
    is_owner_of,
    has_function,
    has_manner_of_functioning,
    has_origin,
    has_production_method,
    has_content,
    has_no_property,
    has_habitat,
    has_location,
    is_location_of,
    has_measure,
    is_measure_of,
    has_material,
    has_member,
    is_member_of,
    has_diet,
    is_diet_of,
    has_eponym,
    has_user,
    has_product,
    is_product_of,
    has_prototypical_holder,
    is_prototypical_holder_for,
    has_prototypical_place_of_usage,
    has_relation,
    has_raw_product,
    has_other_property,
    is_storage_for,
    has_specialization,
    has_part,
    is_part_of,
    has_topic,
    is_caused_by,
    is_cause_for,
    is_comparable_to,
    has_usage,
    has_result_of_usage,
    has_purpose_of_usage,
    has_goods,
    has_time,
    is_access_to,
    has_ingredient,
    is_ingredient_of;

    /**
     * Return true if the <code>String</code> <code>relName</code> represents a
     * valid <code>LexRel</code>.
     * @param relName the name of the relation to verify
     * @return true if the <code>String</code> <code>relName</code> represents
     * a valid <code>LexRel</code>
     */
    public static boolean isRel(String relName) {

        LexRel[] vals = values();

        for (int i = 0; i < vals.length; i++) {
            if (vals[i].toString().equals(relName)) {
                return true;
            }
        }
        return false;
    }
}
