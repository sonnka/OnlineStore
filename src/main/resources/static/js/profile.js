$(document).ready(function () {
    var customerId = $('#customerId').val();
    loadProfile();
    var editProfileButton = $('#editProfileButton');
    var deleteProfileButton = $('#deleteProfileButton');
    var getOrdersButton = $('#getOrdersButton');
    var image = $('#thumbnail');

    $('#fileImage').change(function () {
        showImageThumbnail(this);
    });

    editProfileButton.click(function () {
        window.location = "/profile/edit";
    });
    deleteProfileButton.click(function () {
        deleteProfile();
    });
    getOrdersButton.click(function () {
        window.location = "/profile/orders";
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
        $('#customerNameSurname').text(responseJson.name + " " + responseJson.surname);
        $('#customerName').text(responseJson.name);
        $('#customerSurname').text(responseJson.surname);
        $('#customerEmail').text(responseJson.email);
        $('#customerEmail2').text(responseJson.email);
        $('#amountOfBasketElem').text(responseJson.amountOfBasketElem)
        $('#totalAmountOfOrders').text(responseJson.totalAmountOfOrders);
        $('#totalAmountOfOrders2').text(responseJson.totalAmountOfOrders);
        $('#amountPaidOrders').text(responseJson.amountOfPaidOrders);
        $('#amountUnpaidOrders').text(responseJson.amountOfUnpaidOrders);
        $('#amountGrantedAdmins').text(responseJson.amountOfAddedAdmins);
        image.attr('src', "http://images.example.com/customers/default.png");
        image.attr('onerror', "this.onerror=null;this.src='http://images.example.com/customers/default.png'");
        image.attr('src', "http://images.example.com/customers/" + responseJson.avatar);
    }

    function deleteProfile() {
        $.ajax({
            type: "DELETE",
            url: "/customers/" + customerId,
            success: function () {
                logout();
            },
            error: function (e) {
                console.log(e);
                window.location = "/";
            }
        })
    }

    function logout() {
        $.ajax({
            type: "POST",
            url: "/logout",
            contentType: "application/json",
            dataType: 'json',
            success: function () {
                window.location = "/";
            },
            error: function (e) {
                console.log(e);
                window.location = "/";
            }
        })
    }
});