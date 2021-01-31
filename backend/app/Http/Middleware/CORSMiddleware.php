<?php namespace App\Http\Middleware;

use Closure;

class CORSMiddleware
{
     /**
     * Handle an incoming request and allow CORS.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        $allHeaders = [
            'Access-Control-Allow-Origin'      => '*',
            'Access-Control-Allow-Methods'     => 'POST, GET, OPTIONS, PUT, DELETE',
            'Access-Control-Allow-Credentials' => 'true',
            'Access-Control-Max-Age'           => '86400',
            'Access-Control-Allow-Headers'     => 'Content-Type, Authorization, X-Requested-With'
        ];

        if ($request->isMethod('OPTIONS'))
        {
            return response()->json('{"method":"OPTIONS"}', 200, $allHeaders);
        }

        $finalResponse = $next($request);
        foreach($allHeaders as $key => $value)
        {
            $finalResponse->header($key, $value);
        }

        return $finalResponse;
    }
}
