$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    var sortName = $('#sortName');
    var sortSurname = $('#sortSurname');
    var sortEmail = $('#sortEmail');


    fetchCustomer(0);

    sortName.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "name";
        fetchCustomer(0);
    });
    sortSurname.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "surname";
        fetchCustomer(0);
    });
    sortEmail.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "email";
        fetchCustomer(0);
    });

    function fetchCustomer(startPage) {

        $.ajax({
            type: "GET",
            url: "/admin/customers",
            data: {
                page: startPage,
                size: 7,
                sort: sort + "," + dir
            },
            success: function (response) {
                $('#customersTable tbody').empty();
                $.each(response.content, (i, customer) => {
                    let customerRow = '<tr>' +
                        '<td>' + customer.name + '</td>' +
                        '<td>' + customer.surname + '</td>' +
                        '<td>' + customer.email + '</td>' +
                        '<td><a class="btn btn-primary" href="/profile/orders" >Send</a></td>' +
                        '<td><a class="btn btn-delete" href="/profile/orders/" ><span class="mdi mdi-delete mdi-24px"></span>\n' +
                        '  <span class="mdi mdi-delete-empty mdi-24px"></span>\n' +
                        '  <span>Delete</span></a></td>' +
                        '</tr>';
                    $('#customersTable tbody').append(customerRow);
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
                pagingLink += '<li class="page-item active"><a class="page-link"> ' + i + ' </a></li>'; // no need to create a link to current page
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
        console.log('val: ' + val);

        if (val.toUpperCase() === "«") {
            let currentActive = $("li.active");
            fetchCustomer(0);
            $("li.active").removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchCustomer(totalPages - 1);
            $("li.active").removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = $("li.active");
                startPage = activeValue;
                fetchCustomer(startPage);
                $("li.active").removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchCustomer(startPage);
                let currentActive = $("li.active");
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchCustomer(startPage);
            $("li.active").removeClass("active");
            $(this).parent().addClass("active");
        }
    });

});