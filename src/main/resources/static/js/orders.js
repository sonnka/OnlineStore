$(document).ready(function () {
    let createOrderButton = $('#createOrderButton');
    let customerId = $('#customerId').val();
    let totalPages = 1;
    let sort = "date"
    let dir = "asc";
    let sortDate = $('#sortDate');
    let sortStatus = $('#sortStatus');
    let sortPrice = $('#orderSortPrice');
    let myMessage = document.getElementById("myElement").textContent;

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

        let pageNumber = response.pageable.pageNumber;

        let numLinks = 10;

        let first = '';
        let prev = '';

        if (pageNumber > 0) {
            if (pageNumber !== 0) {
                first = '<li class="page-item"><a class="page-link">«</a></li>';
            }
            prev = '<li class="page-item"><a class="page-link">‹</a></li>';
        } else {
            prev = '';
            first = '';
        }

        let next = '';
        let last = '';

        if (pageNumber < totalPages) {
            if (pageNumber !== totalPages - 1) {
                next = '<li class="page-item"><a class="page-link">›</a></li>';
                last = '<li class="page-item"><a class="page-link">»</a></li>';
            }
        } else {
            next = '';
            last = '';
        }

        let start = pageNumber - (pageNumber % numLinks) + 1;
        let end = start + numLinks - 1;
        end = Math.min(totalPages, end);
        let pagingLink = '';

        for (let i = start; i <= end; i++) {
            if (i === (pageNumber + 1)) {
                pagingLink += '<li class="page-item active"><a class="page-link"> ' + i + ' </a></li>';
            } else {
                pagingLink += '<li class="page-item"><a class="page-link"> ' + i + ' </a></li>';
            }
        }

        pagingLink = first + prev + pagingLink + next + last;

        $("ul.pagination").append(pagingLink);
    }

    $(document).on("click", "ul.pagination li a", function () {
        let val = $(this).text();
        let active = $("li.active");
        console.log('val: ' + val);

        let startPage;
        if (val.toUpperCase() === "«") {
            let currentActive = active;
            fetchOrder(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchOrder(totalPages - 1);
            let currentActive = active;
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