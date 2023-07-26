$(document).ready(function () {
    let temp = '';
    let text = "Subscribe";
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
                    if (product.customer) {
                        text = "Cancel";
                    } else {
                        text = "Subscribe";
                    }
                    let productRow = '<div class="card"><div class="card__header">'
                        + '<img alt="card__image" class="card__image" width="400" height="200" '
                        + 'src="' + product.image + '" '
                        + 'onerror="this.onerror=null;this.src=\'http://images.example.com/products/default_sub.jpg\'">'
                        + '</div>'
                        + '<div class="card__body" style="height: 220px"><h4 id="title">' + product.name + '</h4>'
                        + product.description + '</div>'
                        + '<div class="intro2"><a class="subscribe">'
                        + '<button class="button" data-product-id="' + productId + '">' + text + '</button></a><br>'
                        + '<p id="price" style="color: #009a1e" ><i>' + product.price + '/per '
                        + product.recurring
                        + '</i></p></div></div>';
                    temp += productRow;
                });

                document.getElementsByClassName('container')[0].innerHTML = temp;

                const subscribeButtons = document.getElementsByClassName('button');
                for (let i = 0; i < subscribeButtons.length; i++) {
                    subscribeButtons[i].addEventListener('click', function (e) {
                        alert(this.getAttribute('value'));
                        const productId = this.getAttribute('data-product-id');
                        subscribe(productId)
                        e.stopImmediatePropagation();
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
                window.location = "/subscriptions?success";
            },
            error: function () {
                window.location = "/subscriptions?error";
            }
        });
    }
});