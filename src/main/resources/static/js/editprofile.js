$(document).ready(function () {
    let customerId = $('#customerId').val();
    let saveCustomerButton = $('#saveCustomerButton');
    let nameSurname = $('#nameSurname');
    let name = $('#name');
    let surname = $('#surname');
    let email = $('#email');
    let image = $('#thumbnail');
    let customerImage = "default.png";

    loadProfile();

    saveCustomerButton.click(function () {
        updateCustomer();
    });

    $('#imageForm').submit(function (event) {
        event.preventDefault();
        let formData = new FormData();
        formData.append('file', $('#imageFile')[0].files[0]);
        $.ajax({
            type: 'POST',
            url: '/customers/' + customerId + '/upload',
            data: formData,
            processData: false,
            contentType: false,
            success: function () {
                loadProfile();
            },
            error: function () {
                loadProfile();
            }
        });
    });

    function loadProfile() {
        let url = "/customers/" + customerId;

        $.get(url, function (responseJson) {
            displayData(responseJson)
        }).fail(function () {
            window.location = "/";
        });
    }

    function displayData(responseJson) {
        nameSurname.text(responseJson.name + " " + responseJson.surname);
        name.val(responseJson.name);
        surname.val(responseJson.surname);
        email.text(responseJson.email);
        image.attr('src', "http://images.example.com/customers/default.png");
        image.attr('onerror', "this.onerror=null;this.src='http://images.example.com/customers/default.png'");
        image.attr('src', "http://images.example.com/customers/" + responseJson.avatar);
        customerImage = responseJson.avatar;
    }

    function updateCustomer() {
        let jsonData = {
            "id": customerId,
            "name": name.val(),
            "surname": surname.val(),
            "email": email.val(),
            "avatar": customerImage
        };

        $.ajax({
            type: "PATCH",
            url: "/customers/" + customerId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = "/profile";
            }
        }).fail(function () {
            window.location = "/profile/edit?error";
        });
    }

});