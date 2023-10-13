package kazantseva.project.OnlineStore.model.request;

import lombok.Data;

@Data
public class AuthRequest {

    private String email;
    private String password;
}
