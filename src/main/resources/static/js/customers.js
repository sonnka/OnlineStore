$(document).ready(function () {
    let totalPages = 1;
    let sort = "name"
    let dir = "asc";
    let sortId = $('#sortId');
    let sortName = $('#sortName');
    let sortSurname = $('#sortSurname');
    let sortEmail = $('#sortEmail');
    let table = $('#customersTable tbody');


    fetchCustomer(0);

    sortId.click(function () {
        dir = dir === "asc" ? "desc" : "asc";
        sort = "id";
        fetchCustomer(0);
    });

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
                table.empty();
                $.each(response.content, (i, customer) => {
                    let customerId = customer.id;
                    let customerRow = '<tr>' +
                        '<td><div class="text-left">' + customerId + '</div></td>' +
                        '<td>' + customer.name + '</td>' +
                        '<td>' + customer.surname + '</td>' +
                        '<td>' + customer.email + '</td>' +
                        '<td class="actions" data-th=""> ' +
                        '<a type="button" title="resend letter" class="resendLetter">' +
                        '<i class="material-icons send">&#xe0be;</i></a>&emsp;' +
                        '<a type="button" title="make admin" class="makeAdmin">' +
                        '<i class="material-icons admin">&#xe7fd;</i></a></div> &emsp;' +
                        '<a type="button" title="delete" class="deleteCustomer">' +
                        '<i class="material-icons">&#xE872;</i></a></div></td> ' +
                        '</tr>';
                    table.append(customerRow);
                });
                table.on('click', '.resendLetter', function () {
                    let customerId = $(this).closest('tr').find('.text-left').text();
                    resendLetter(customerId);
                });
                table.on('click', '.makeAdmin', function () {
                    let customerId = $(this).closest('tr').find('.text-left').text();
                    makeAdmin(customerId);
                });
                table.on('click', '.deleteCustomer', function () {
                    let customerId = $(this).closest('tr').find('.text-left').text();
                    deleteCustomer(customerId);
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

    function resendLetter(customerId) {
        $.ajax({
            type: "GET",
            url: "/admin/customers/" + customerId + "/resend",
            success: function () {
                window.location = "/admin/customers_list?success_send";
            }
        }).fail(function () {
            window.location = "/admin/customers_list?error_send";
        });
    }

    function makeAdmin(customerId) {
        $.ajax({
            type: "PATCH",
            url: "/admin/customers/" + customerId + "/admin",
            success: function () {
                window.location = "/admin/customers_list?success_admin";
            }
        }).fail(function () {
            window.location = "/admin/customers_list?error_admin";
        });
    }

    function deleteCustomer(customerId) {
        $.ajax({
            type: "DELETE",
            url: "/customers/" + customerId,
            success: function () {
                window.location = "/admin/customers_list?success_delete";
            }
        }).fail(function () {
            window.location = "/admin/customers_list?error_delete";
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
            fetchCustomer(0);
            active.removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "»") {
            fetchCustomer(totalPages - 1);
            let currentActive = active;
            $("li.active").removeClass("active");
            currentActive.next().addClass("active");

        } else if (val.toUpperCase() === "›") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue < totalPages) {
                let currentActive = active;
                startPage = activeValue;
                fetchCustomer(startPage);
                active.removeClass("active");
                currentActive.next().addClass("active");
            }
        } else if (val.toUpperCase() === "‹") {
            let activeValue = parseInt($("ul.pagination li.active").text());
            if (activeValue > 1) {
                startPage = activeValue - 2;
                fetchCustomer(startPage);
                let currentActive = active;
                currentActive.removeClass("active");
                currentActive.prev().addClass("active");
            }
        } else {
            startPage = parseInt(val - 1);
            fetchCustomer(startPage);
            active.removeClass("active");
            $(this).parent().addClass("active");
        }
    });
});