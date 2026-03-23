package com.configcenter.backend.common.exception;

import java.util.List;

public record ErrorResponseView(List<ErrorDetail> details) {
}
