$(document).ready(function () {
    var productId = $('#productId').val();
    var saveProductButton = $('#saveProductButton');

    var name = $('#name');
    var price = $('#price');

    if (productId === "-1") {
        createProduct();
    } else {
        loadProduct();
    }

    $('#fileImage').change(function () {
        showImageThumbnail(this);
    });


    saveProductButton.click(function () {
        updateProduct();
    });

    function showImageThumbnail(fileInput) {
        file = fileInput.files[0];
        reader = new FileReader();

        reader.onload = function (e) {
            $('#thumbnail').attr('src', e.target.result);
        };

        reader.readAsDataURL(file);
    }

    function createProduct() {

    }

    function loadProduct() {
        url = "/admin/products/" + productId;

        $.get(url, function (responseJson) {
            displayData(responseJson)
        }).fail(function () {
            window.location = "/";
        });
    }

    function displayData(responseJson) {
        name.val(responseJson.name);
        price.val(responseJson.price);
    }

    function updateProduct() {
        jsonData = {
            "id": productId,
            "name": name.val(),
            "image": image.val(),
            "price": price.val(),
        };

        $.ajax({
            type: "PATCH",
            url: "/admin/products/" + productId,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = "/products_html";
            }
        }).fail(function () {
            window.location = "/products_html";
        });
    }

});