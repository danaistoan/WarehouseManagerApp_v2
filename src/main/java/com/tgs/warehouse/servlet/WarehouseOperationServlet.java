package com.tgs.warehouse.servlet;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tgs.warehouse.dao.LogisticUnitDAO;
import com.tgs.warehouse.entities.ProductPallet;
import com.tgs.warehouse.services.WarehouseService;
import com.tgs.warehouse.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class WarehouseOperationServlet
 */
@WebServlet("/warehouseOperations")
public class WarehouseOperationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public WarehouseOperationServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JQueryDataTableParamModel param = DataTablesParamUtil.getParam(request);

        LogisticUnitDAO logisticUnit = new LogisticUnitDAO();
        List<ProductPallet> allPallets = logisticUnit.getAllPallets();

        if(param.searchValue != null && param.searchValue != ""){
            searchPalletByDescriptionJsonNew(response, param, allPallets, logisticUnit);
        }
        getAllPalletsJsonNew(request, response, param, allPallets);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        LogisticUnitDAO logisticUnit = new LogisticUnitDAO();

        switch (action) {

            case "deletePallet":
                deletePallet(request, response, logisticUnit);
                break;

            case "addPallet":
                savePalletFromJson(request, response, logisticUnit);
                break;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action param!");
                break;
        }
    }

    private void deletePallet(HttpServletRequest request, HttpServletResponse response, LogisticUnitDAO logisticUnit)
            throws ServletException, IOException {

        Long palletId = Long.parseLong(request.getParameter("palletId"));
        boolean palletDeleted = logisticUnit.deleteProductPallet(palletId);

        if (palletDeleted) {
            System.out.println("The pallet with id " + palletId + " was deleted from the warehouse");
        } else {
            System.out.println("The pallet could not be deleted");
        }
    }

    private void writeToJson(Object value, OutputStream outputStream) {

        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(outputStream, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePalletFromJson(HttpServletRequest request, HttpServletResponse response, LogisticUnitDAO logisticUnit){

        ObjectMapper mapper = new ObjectMapper();
        ProductPallet pallet = new ProductPallet();

        try {
            String jsonPallet = request.getParameter("palletJson");
            pallet = mapper.readValue(jsonPallet, ProductPallet.class);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProductPallet savedPallet = logisticUnit.insertProductPallet(pallet);
    }

    private void getAllPalletsJsonNew(HttpServletRequest request, HttpServletResponse response, JQueryDataTableParamModel param, List<ProductPallet> allPallets) throws IOException {

        int allPalletsSize = allPallets.size();
        List<ProductPallet> resultPallets = allPallets.subList(param.start, Math.min(param.start + param.length, allPalletsSize));

        int iTotalRecords = allPalletsSize;
        int iTotalDisplayRecords = allPalletsSize;

        writePalletToJson(response, iTotalRecords, iTotalDisplayRecords, resultPallets);
    }

    private void searchPalletByDescriptionJsonNew(HttpServletResponse response, JQueryDataTableParamModel param, List<ProductPallet> allPallets, LogisticUnitDAO logisticUnit) throws IOException {

        List<ProductPallet> foundPalletList = logisticUnit.search(param.searchValue);

        int foundPalletsSize = foundPalletList.size();
        int iTotalRecords = allPallets.size();
        int iTotalDisplayRecords = foundPalletsSize;
        List<ProductPallet> resultPallets = foundPalletList.subList(param.start, Math.min(param.start + param.length, foundPalletsSize));

        writePalletToJson(response, iTotalRecords, iTotalDisplayRecords, resultPallets);
    }

    private void writePalletToJson(HttpServletResponse response, int iTotalRecords, int iTotalDisplayRecords, List<ProductPallet> resultPallets) throws IOException {

        response.setContentType("application/json");
        ServletOutputStream outputStream = response.getOutputStream();

        Map palletsDataTableMap = new HashMap();
        palletsDataTableMap.put("iTotalRecords", iTotalRecords);
        palletsDataTableMap.put("iTotalDisplayRecords", iTotalDisplayRecords);
        palletsDataTableMap.put("palletList", resultPallets);

        writeToJson(palletsDataTableMap, outputStream);
    }
}


