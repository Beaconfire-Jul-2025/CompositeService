package org.beaconfire.composite.helper;

import org.beaconfire.composite.dto.ApiResponse;
import org.beaconfire.composite.dto.PageListResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper utility class for handling ApiResponse and pagination conversions
 */
public class ApiResponseHelper {

    /**
     * Extracts data from ApiResponse wrapper after validating success status
     *
     * @param response The ResponseEntity containing ApiResponse
     * @param <T>      The type of data expected
     * @return The extracted data
     * @throws RuntimeException if the response is not successful
     */
    public static <T> T extractData(ResponseEntity<ApiResponse<T>> response) {
        if (response == null || response.getBody() == null) {
            throw new RuntimeException("Response is null or empty");
        }

        ApiResponse<T> apiResponse = response.getBody();

        if (!apiResponse.isSuccess()) {
            throw new RuntimeException("API call failed: " +
                    apiResponse.getErrorMessage() + " (Code: " + apiResponse.getErrorCode() + ")");
        }

        return apiResponse.getData();
    }

    /**
     * Extracts and transforms paginated data from ApiResponse
     *
     * @param response    The ResponseEntity containing ApiResponse with PageListResponse
     * @param transformer Function to transform each item in the list
     * @param <T>         The original type of items in the list
     * @param <R>         The transformed type
     * @return PageListResponse with transformed data
     */
    public static <T, R> PageListResponse<R> extractAndTransformPageData(
            ResponseEntity<ApiResponse<PageListResponse<T>>> response,
            Function<T, R> transformer) {

        PageListResponse<T> originalPage = extractData(response);

        List<R> transformedList = originalPage.getList().stream()
                .map(transformer)
                .collect(Collectors.toList());

        return PageListResponse.<R>builder()
                .list(transformedList)
                .current(originalPage.getCurrent())
                .pageSize(originalPage.getPageSize())
                .total(originalPage.getTotal())
                .build();
    }

    /**
     * Extracts paginated data from ApiResponse
     *
     * @param response The ResponseEntity containing ApiResponse with PageListResponse
     * @param <T>      The type of items in the list
     * @return PageListResponse object
     */
    public static <T> PageListResponse<T> extractPageData(ResponseEntity<ApiResponse<PageListResponse<T>>> response) {
        return extractData(response);
    }

    /**
     * Safely extracts data from ApiResponse, returning null if not successful
     *
     * @param response The ResponseEntity containing ApiResponse
     * @param <T>      The type of data expected
     * @return The extracted data or null if not successful
     */
    public static <T> T extractDataSafely(ResponseEntity<ApiResponse<T>> response) {
        try {
            return extractData(response);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Checks if the ApiResponse indicates success
     *
     * @param response The ResponseEntity containing ApiResponse
     * @return true if successful, false otherwise
     */
    public static boolean isSuccessful(ResponseEntity<ApiResponse<?>> response) {
        return response != null &&
                response.getBody() != null &&
                response.getBody().isSuccess();
    }

    /**
     * Gets error message from ApiResponse
     *
     * @param response The ResponseEntity containing ApiResponse
     * @return Error message or null if successful
     */
    public static String getErrorMessage(ResponseEntity<ApiResponse<?>> response) {
        if (response == null || response.getBody() == null) {
            return "Response is null or empty";
        }

        ApiResponse<?> apiResponse = response.getBody();
        return apiResponse.isSuccess() ? null : apiResponse.getErrorMessage();
    }
}