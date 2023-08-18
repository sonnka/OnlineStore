$(document).ready(function () {
    let emailField = $('#email');
    let passwordFiled = $('#password');
    let submitButton = $('#submit');

    submitButton.click(function (event) {
        event.preventDefault();
        login();
    });

    function login() {

        let formData = new URLSearchParams();

        formData.append('client_id', "online-store-app");
        formData.append('username', emailField.val());
        formData.append('password', passwordFiled.val());
        formData.append('grant_type', "password");

        formData.forEach(p => console.log(p));

        $.ajax({
            type: 'POST',
            url: "http://localhost:8080/realms/SpringBootKeycloak/protocol/openid-connect/token",
            data: formData.toString(),
            processData: false,
            contentType: "application/x-www-form-urlencoded",
            success: function (response) {
                alert("success");
            },
            error: function (xhr, textStatus, errorThrown) {
                alert("Login error");
                console.log(xhr, textStatus, errorThrown);
            }
        });
    }
})