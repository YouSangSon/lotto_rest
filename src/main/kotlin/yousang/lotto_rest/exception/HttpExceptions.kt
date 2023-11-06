package yousang.lotto_rest.exception

// 4xx 에러
class BadRequestException(message: String, error: ErrorCode? = null) : HttpException(400, message, error)
class UnauthorizedException(message: String, error: ErrorCode? = null) : HttpException(401, message, error)
class ForbiddenException(message: String, error: ErrorCode? = null) : HttpException(403, message, error)
class NotFoundException(message: String, error: ErrorCode? = null) : HttpException(404, message, error)
class MethodNotAllowedException(message: String, error: ErrorCode? = null) : HttpException(405, message, error)
class NotAcceptableException(message: String, error: ErrorCode? = null) : HttpException(406, message, error)
class RequestTimeoutException(message: String, error: ErrorCode? = null) : HttpException(408, message, error)
class ConflictException(message: String, error: ErrorCode? = null) : HttpException(409, message, error)
class GoneException(message: String, error: ErrorCode? = null) : HttpException(410, message, error)
class PreconditionFailedException(message: String, error: ErrorCode? = null) : HttpException(412, message, error)
class PayloadTooLargeException(message: String, error: ErrorCode? = null) : HttpException(413, message, error)
class UnsupportedMediaTypeException(message: String, error: ErrorCode? = null) : HttpException(415, message, error)
class ATeapotException(message: String, error: ErrorCode? = null) : HttpException(418, message, error)
class MisdirectedException(message: String, error: ErrorCode? = null) : HttpException(421, message, error)
class UnprocessableEntityException(message: String, error: ErrorCode? = null) : HttpException(422, message, error)

// 5xx 에러
class InternalServerErrorException(message: String, error: ErrorCode? = null) : HttpException(500, message, error)
class NotImplementedException(message: String, error: ErrorCode? = null) : HttpException(501, message, error)
class BadGatewayException(message: String, error: ErrorCode? = null) : HttpException(502, message, error)
class ServiceUnavailableException(message: String, error: ErrorCode? = null) : HttpException(503, message, error)
class GatewayTimeoutException(message: String, error: ErrorCode? = null) : HttpException(504, message, error)
class HttpVersionNotSupportedException(message: String, error: ErrorCode? = null) : HttpException(505, message, error)