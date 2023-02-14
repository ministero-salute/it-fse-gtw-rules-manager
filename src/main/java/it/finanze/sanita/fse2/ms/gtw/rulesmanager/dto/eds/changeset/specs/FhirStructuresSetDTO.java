/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs;

import lombok.Data;

import java.util.List;

@Data
public class FhirStructuresSetDTO {
	List<String> templateIdRoot;
	String version;
}
