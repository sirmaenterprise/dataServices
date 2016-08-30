package gov.nga.integration.cspace;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import gov.nga.entities.art.DataNotReadyException;

@ControllerAdvice
public class ExceptionErrorMapper {

	private static final Logger log = LoggerFactory.getLogger(ExceptionErrorMapper.class);
	
	@ExceptionHandler(APIUsageException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorLoggerResponse handleAPIUsageException(Exception e, HttpServletRequest req){
		log.warn(e.getMessage(),e);
		return new ErrorLoggerResponse(
				"error", req.getRequestURI(), 
				"There was a problem with the request that prevented it from being processed at all. " + GenericErrorController.HELPLINK, 
				e.getMessage()
		);
	}

	@ExceptionHandler(SQLException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	ErrorLoggerResponse handleAPIUsageException(SQLException e, HttpServletRequest req){
		log.warn(e.getMessage(),e);
		return new ErrorLoggerResponse(
				"error", req.getRequestURI(), 
				"A SQL Exception was thrown when processing the request. ", 
				e.getMessage()
		);
	}

	@ExceptionHandler(ExecutionException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	ErrorLoggerResponse handleAPIUsageException(ExecutionException e, HttpServletRequest req){
		log.warn(e.getMessage(),e);
		return new ErrorLoggerResponse(
				"error", req.getRequestURI(), 
				"An Execution Exception (an exception thrown from a spawned thread) was thrown when processing the request. ", 
				e.getMessage()
		);
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	ErrorLoggerResponse handleAPIUsageException(IOException e, HttpServletRequest req){
		log.warn(e.getMessage(),e);
		return new ErrorLoggerResponse(
				"error", req.getRequestURI(), 
				"An IO Exception was thrown when processing the request. ", 
				e.getMessage()
		);
	}

	@ExceptionHandler(DataNotReadyException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	ErrorLoggerResponse handleDataNotReadyException(DataNotReadyException e, HttpServletRequest req){
		log.warn(e.getMessage(),e);
		return new ErrorLoggerResponse(
				"error", req.getRequestURI(), 
				"The data service is still starting up and not yet ready to handle requests.", 
				e.getMessage()
		);
	}

	@ExceptionHandler(TypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorLoggerResponse handleTypeMismatchException(TypeMismatchException e, HttpServletRequest req){
		return handleAPIUsageException(e, req);
	}

/*	@ExceptionHandler({UsernameNotFoundException.class})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	SimpleErrorMessage handleException(UsernameNotFoundException exception){
		log.debug("Username not found {}",exception.getLocalizedMessage());
		log.trace(exception.getMessage(),exception);
		return new SimpleErrorMessage("Unaouthorized"," ");
	}
*/

}
