$(document).ready(function () {
    let temp = '';
    loadSubscriptions();

    function loadSubscriptions() {
        $.ajax({
            type: "GET",
            url: "/stripe/products",
            data: {
                limit: 10
            },
            contentType: 'application/json',
            success: function (response) {
                temp = '';
                $.each(response, (i, product) => {
                    let productId = product.id;
                    let productRow = '<div class="card"><div class="card__header">'
                        + '<img alt="card__image" class="card__image" width="550" src="' + product.image + '"></div>'
                        + '<div class="card__body" style="height: 220px"><h4 id="title">' + product.name + '</h4>'
                        + product.description
                        + '</div><div class="intro2"><a class="subscribe"><button class="button" data-product-id="' + productId + '">Subscribe</button></a><br>'
                        + '<p id="price" style="color: #009a1e" ><i>' + product.price + '/per '
                        + product.recurring
                        + '</i></p></div></div>';
                    temp += productRow;
                });
                document.getElementsByClassName('container')[0].innerHTML = temp;

                const subscribeButtons = document.getElementsByClassName('button');
                for (let i = 0; i < subscribeButtons.length; i++) {
                    subscribeButtons[i].addEventListener('click', function () {
                        const productId = this.getAttribute('data-product-id');
                        subscribe(productId)
                    });
                }

            },
            error: function () {
                window.location = "/";
            }
        });
    }


    function subscribe(productId) {
        $.ajax({
            type: "POST",
            url: "/stripe/subscription/" + productId,
            success: function () {
                window.location = "/subscriptions_html?success";
            },
            error: function () {
                window.location = "/subscriptions_html?error";
            }
        });
    }

});