$(document).ready(function () {
    var customerId = $('#customerId').val();
    var saveCustomerButton = $('#saveCustomerButton');

    var nameSurname = $('#nameSurname');
    var name = $('#name');
    var surname = $('#surname');
    var email = $('#email');

    loadProfile();

    $('#fileImage').change(function () {
        showImageThumbnail(this);
    });


    saveCustomerButton.click(function () {
        updateCustomer();
    });

    function showImageThumbnail(fileInput) {
        file = fileInput.files[0];
        reader = new FileReader();

        reader.onload = function (e) {
            $('#thumbnail').attr('src', e.target.result);
        };

        reader.readAsDataURL(file);
    }

    function loadProfile() {
        url = "/customers/" + customerId;

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
    }

    function updateCustomer() {
        jsonData = {
            "id": customerId,
            "name": name.val(),
            "surname": surname.val(),
            "email": email.val(),
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