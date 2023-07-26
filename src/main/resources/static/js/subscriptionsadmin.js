$(document).ready(function () {
    let temp = '';
    let addButton = $('#addButton');
    let activeSubList = $('#activeSubList');
    let archiveSubList = $('#archiveSubList');

    addButton.click(function () {
        createSub();
    });

    activeSubList.click(function () {
        loadActive(true);
    });

    archiveSubList.click(function () {
        loadActive(false);
    });

    loadActive(true);

    function loadSubscriptions() {
        $.ajax({
            type: "GET",
            url: "/stripe/products",
            data: {
                limit: 15
            },
            contentType: 'application/json',
            success: function (response) {
                temp = '';
                $.each(response, (i, subscription) => {
                    let subscriptionId = subscription.id;
                    let productRow = '<div class="card" data-subscription-id="' + subscriptionId + '">'
                        + '<div style="background-color: #dadada">&emsp;'
                        + '<a type="button" title="edit" class="editSub" >'
                        + '<i class="material-icons admin">&#xe3c9;</i></a>&emsp;'
                        + '<a type="button" title="archive" class="archiveSub" >'
                        + '<i class="material-icons archive">&#xe149;</i></a></div>'
                        + '<div class="card__header">'
                        + '<img alt="card__image" class="card__image" width="400" height="200" '
                        + 'src="' + subscription.image + '"'
                        + 'onerror="this.onerror=null;this.src=\'http://images.example.com/products/default_sub.jpg\'">'
                        + '</div>'
                        + '<div class="card__body" style="height: 220px"><h4 id="title">' + subscription.name + '</h4>'
                        + subscription.description + '</div><br>'
                        + '<div class="intro2"><p id="price" style="color: #009a1e" ><i>' + subscription.price + '/per '
                        + subscription.recurring
                        + '</i></p></div>'
                        + '</div>';
                    temp += productRow;
                });
                document.getElementsByClassName('container')[0].innerHTML = temp;

                let container = $('.container');

                container.on('click', '.editSub', function (e) {
                    let subscriptionId = $(this).closest('.card').data('subscription-id');
                    editSub(subscriptionId);
                    e.stopImmediatePropagation();
                });
                container.on('click', '.archiveSub', function (e) {
                    let subscriptionId = $(this).closest('.card').data('subscription-id');
                    archiveSub(subscriptionId);
                    e.stopImmediatePropagation();
                });

            },
            error: function () {
                window.location = "/";
            }
        });
    }

    function loadArchiveSubscriptions() {
        $.ajax({
            type: "GET",
            url: "/stripe/products/archive",
            data: {
                limit: 15
            },
            contentType: 'application/json',
            success: function (response) {
                temp = '';
                $.each(response, (i, subscription) => {
                    let subscriptionId = subscription.id;
                    let productRow = '<div class="card" data-subscription-id="' + subscriptionId + '">'
                        + '<div style="background-color: #dadada">&emsp;'
                        + '<a type="button" title="archive" class="archiveSub" >'
                        + '<i class="material-icons admin">&#xe149;</i></a></div>'
                        + '<div class="card__header">'
                        + '<img alt="card__image" class="card__image" width="400" height="200" '
                        + 'src="' + subscription.image + '"'
                        + 'onerror="this.onerror=null;this.src=\'http://images.example.com/products/default_sub.jpg\'">'
                        + '</div>'
                        + '<div class="card__body" style="height: 220px"><h4 id="title">' + subscription.name + '</h4>'
                        + subscription.description + '</div><br>'
                        + '<div class="intro2"><p id="price" style="color: #009a1e" ><i>' + subscription.price + '/per '
                        + subscription.recurring
                        + '</i></p></div>'
                        + '</div>';
                    temp += productRow;
                });
                document.getElementsByClassName('container')[0].innerHTML = temp;

                let container = $('.container');

                container.on('click', '.archiveSub', function (e) {
                    let subscriptionId = $(this).closest('.card').data('subscription-id');
                    archiveSub(subscriptionId);
                    e.stopImmediatePropagation();
                });

            },
            error: function () {
                window.location = "/";
            }
        });
    }

    function editSub(subscriptionId) {
        window.location = "/admin/subscriptions/" + subscriptionId;
    }

    function archiveSub(subscriptionId) {
        $.ajax({
            type: 'PATCH',
            url: '/stripe/products/' + subscriptionId + '/archive',
            success: function () {
                loadActive(true)
            },
            error: function () {
                window.location = "/subscriptions?error"
            }
        });
    }

    function createSub() {
        window.location = "/admin/subscriptions";
    }

    function loadActive(isActive) {
        if (isActive) {
            document.getElementById("activeSubList").classList.add("active");
            document.getElementById("archiveSubList").classList.remove("active");
            loadSubscriptions();
        } else {
            document.getElementById("archiveSubList").classList.add("active");
            document.getElementById("activeSubList").classList.remove("active");
            loadArchiveSubscriptions();
        }
    }

});