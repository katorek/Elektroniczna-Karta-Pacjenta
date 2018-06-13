package app.connection;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.dstu3.model.BaseResource;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

//import ca.uhn.fhir.model.primitive.IdDt;

/**
 * Created by Wojciech Jaronski
 */

class GenericHapi<T extends BaseResource> {
    private static final int LIST_SIZE = 50;
    private Class<T> classType;

    private HapiConnection hc;
    private FhirContext ctx;

    GenericHapi(HapiConnection hc, Class<T> classType) {
        this.classType = classType;
        this.hc = hc;
    }

    List<T> getList() {
        List<T> list = null;
        try {
            IBaseBundle result = hc.getClient()
                    .search()
                    .forResource(classType)
                    .returnBundle(Bundle.class)
//                    .prettyPrint()
                    .count(LIST_SIZE)
                    .execute();

            int total = ((Bundle) result).getTotal();

            list = new ArrayList<>(total);


            while (list.size() < total) {
                list.addAll(((Bundle) result)
                        .getEntry().stream().map(e -> ((T) e.getResource())).collect(toList()));
                if (list.size() < total)
                    result = hc.getClient().loadPage().next(result).execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
