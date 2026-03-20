<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;


/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::post('/login', function (Request $request) {
    try {
        $token = trim($request->input('user_token'));
        
        // Log for debugging
        $dbHost = config('database.connections.mysql.host');
        $dbPort = config('database.connections.mysql.port');
        \Illuminate\Support\Facades\Log::info("Login attempt: token='$token', host='$dbHost', port='$dbPort'");

        $user = \App\Models\User::where('user_token', $token)->first();

        if ($user) {
            // Create Sanctum token
            $authToken = $user->createToken('auth_token')->plainTextToken;
            
            return response()->json([
                'token' => $authToken,
                'user' => $user
            ]);
        }

        \Illuminate\Support\Facades\Log::warning("Login failed for token: '" . $token . "'");
        return response()->json(['message' => 'Invalid token'], 401);
    } catch (\Exception $e) {
        \Illuminate\Support\Facades\Log::error("Login error: " . $e->getMessage());
        return response()->json([
            'message' => 'Server Error',
            'error' => $e->getMessage()
        ], 500);
    }
});

use App\Http\Controllers\UserController;

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/user-profile', [UserController::class, 'index']);
    Route::post('/update-avatar', [UserController::class, 'updateAvatar']);
    Route::get('/all-data', [UserController::class, 'getAllData']);
    
    Route::get('/user', function (Request $request) {
        return $request->user();
    });
});
