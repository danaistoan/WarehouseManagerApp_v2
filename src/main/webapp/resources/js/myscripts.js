function format(currentPallet) {
    return '<div class="childTableTitle">Packages</div>' +
            '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
                '<tr>' +
                    '<td>Id</td>' +
                    '<td>Description</td>' +
                    '<td>Type</td>' +
                '</tr>' +
                getPackageRows(currentPallet) +
            '</table>';
}

function getPackageRows(pallet) {
    var resultPackages = "";
    for(var i=0; i<pallet.packages.length; i++){
        resultPackages +=
        '<tr>' +
            '<td>' + pallet.packages[i].id + '</td>' +
            '<td>' + pallet.packages[i].description + '</td>' +
            '<td>' + pallet.packages[i].type + '</td>' +
        '</tr>'
    }
    return resultPackages;
}

function addPallet(){
    var newPalletForm = $( "#dialog-form" ).dialog({
        autoOpen: false,
        height: 400,
        width: 400,
        modal: true,
        buttons: {
            "Save": savePallet,
            "Cancel": function() {
                newPalletForm.dialog( "close" );
                $("#dialog-form").trigger('reset');
            },
            "Add new package": function(e){
                e.preventDefault();
                var max_fields = 10;
                var x = 2; //initlal text box count
                if(x < max_fields){ //max input box allowed
                    x++; //text box increment
                    $("#dialog-form").append('<label for="packDesc">Package description</label>');
                    $("#dialog-form").append('<div><input type="text" name="packDesc"+(x) class="text ui-widget-content ui-corner-all"/></div>'); //add input box
                    //$("#dialog-form").append('<br>'); //add line break
                    $("#dialog-form").append('<label for="packType">Package type</label>');
                    $("#dialog-form").append('<div><input type="text" name="packType"+(x) class="text ui-widget-content ui-corner-all"/></div>');
                }
            }
        }
    });
    newPalletForm.dialog( "open" );
}

function savePallet(){
    var pallet = {
        description: $('#description').val(),
        packages: [
            {
                description: $('#pack1Desc').val(),
                type: $('#pack1Type').val()
            },
            {
                description: $('#pack2Desc').val(),
                type: $('#pack2Type').val()
            }
        ]
    };

    $.post("/WarehouseManager/warehouseOperations?action=addPallet",
        {
            palletJson: JSON.stringify(pallet)
        },
        function () {
            $("#dialog-form").dialog("close");
            table.ajax.reload();
        }
    );
}

function loadDataTable(){
    table = $("#pallets_table").DataTable({
        "serverSide": true,
        "ajax": {url: '/WarehouseManager/warehouseOperations', dataSrc: 'palletList'},
        //"ajaxDataProp": "",
        "processing": true,
        //"ordering": true,
        //"sPaginationType": "full_numbers",
        "bJQueryUI": true,
        "columns": [
            {
                "className": 'details-control',
                "orderable": false,
                "data": null,
                "defaultContent": ''
            },
            {"data": "id"},
            {"data": "description"},
            {
                "className": 'delete-control',
                "orderable": false,
                "data": null,
                "defaultContent": ''
            }
        ]
    });

    $('#pallets_table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);

        if (row.child.isShown()) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            row.child(format(row.data())).show();
            tr.addClass('shown');
        }
    });

    $('#pallets_table tbody').on('click', 'td.delete-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);
        $.post("/WarehouseManager/warehouseOperations?action=deletePallet", { palletId: row.data().id}, function(){
            table.ajax.reload();
        });
    });
}





