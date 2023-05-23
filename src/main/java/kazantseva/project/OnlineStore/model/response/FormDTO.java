package kazantseva.project.OnlineStore.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FormDTO {
    private List<Long> selectedItems;
}
