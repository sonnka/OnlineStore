$(document).ready(function () {
    var createOrderButton = $('#createOrderButton');
    var customerId = $('#customerId').val();
    let totalPages = 1;
    let sort = "date"
    let dir = "asc";
    var sortDate = $('#sortDate');
    var sortStatus = $('#sortStatus');
    var sortPrice = $('#orderSortPrice');
    var myMessage = document.getElementById("myElement").textContent;

    sortDate.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "date";
        fetchOrder(0);
    });
    sortStatus.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "status";
        fetchOrder(0);
    });
    sortPrice.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "price";
        fetchOrder(0);
    });

    createOrderButton.click(function () {
        window.location = "/profile/orders/create";
    });

    function fetchOrder(startPage) {

        $.ajax({
            type: "GET",
            url: "/customers/" + customerId + "/orders",
            data: {
                page: startPage,
                size: 5,
                sort: sort + "," + dir
            },
            success: function (response) {
                $('#ordersTable tbody').empty();
                $.each(response.content, (i, order) => {
                    let orderRow = '<tr>' +
                        '<td>' + order.date + '</td>' +
                        '<td>' + order.status + '</td>' +
                        '<td>' + order.price + '</td>' +
                        '<td><a class="btn btn-primary" href="/profile/orders/' + order.id + '" >' + myMessage + '</a></td>' +
                        '</tr>';
                    $('#ordersTable tbody').append(orderRow);
                });

                if ($('ul.pagination li').length - 2 !== response.totalPages) {
                    $('ul.pagination').empty();
                    buildPagination(response);
                }
            },
            error: function (e) {
                console.log("ERROR: ", e);
                window.location = "/";
            }
        });
    }

    function buildPagination(response) {
        totalPages = response.totalPages;

        var pageNumber = response.pageable.pageNumber;

        var numLinks = 10;

        var first = '';
        var prev = '';
        if (pageNumber > 0) {
            if (pageNumber !== 0) {
                first = '<li class="page-item"><a class="page-link">«</a></li>';
            }
            prev = '<li class="page-item"><a class="page-link">‹</a></li>';
        } else {
            prev = '';
            first = '';
        }

        var next = '';
        var last = '';
        if (pageNumber < totalPages) {
            if (pageNumber !== totalPages - 1) {
                next = '<li class="page-item"><a class="page-link">›</a></li>';
                last = '<li class="page-item"><a class="page-link">»</a></li>';
            }
        } else {
            next = '';
            last = '';
        }

        var start = pageNumber - (pageNumber % numLinks) + 1;
        var end = start + numLinks - 1;
        end = Math.min(totalPages, end);
        var pagingLink = '';

        for (var i = start; i <= end; i++) {
            if (i == (pageNumber + 1)) {
                pagingLink += '<li class="page-item active"><a class="page-link"> ' + i + ' </a></li>';
            } else {
                pagingLink += '<li class="page-item"><a class="page-link"> ' + i + ' </a></li>';
            }
        }

        pagingLink = first + prev + pagingLink + next + last;

        $("ul.pagination").append(pagingLink);
    }

    $(document).on("click", "ul.pagination li a", function () {
        var data = $(this).attr('data');
        let val = $(this).text();
        var active = $("li.active");
        console.log('val: ' + val);

        if (val.toUpperCase() === "«") {
            let currentActive = active;
            fetchOrder(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchOrder(totalPages - 1);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchOrder(startPage);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchOrder(startPage);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchOrder(startPage);
            $("li.active").removeClass("active");
            $(this).parent().addClass("active");
        }
    });

    (function () {
        fetchOrder(0);
    })();
});