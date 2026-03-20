<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ApiTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Test login and data retrieval with token.
     */
    public function test_authenticated_user_can_access_all_data(): void
    {
        // 1. Create a user
        $user = User::create([
            'user_token' => 'test-token',
            'first_name' => 'Ivan',
            'last_name' => 'Ivanov',
            'surname' => 'Ivanovich',
            'educational_organization' => 'MSU',
            'group' => 'A1',
            'course' => '1',
        ]);

        // 2. Test login
        $response = $this->postJson('/api/login', [
            'user_token' => 'test-token',
        ]);

        $response->assertStatus(200)
                 ->assertJsonStructure(['token', 'user']);

        $token = $response->json('token');

        // 3. Test get all data with token
        $response = $this->withHeader('Authorization', 'Bearer ' . $token)
                         ->getJson('/api/all-data');

        $response->assertStatus(200)
                 ->assertJson([
                     'user' => [
                         'id' => $user->id,
                         'user_token' => 'test-token',
                     ]
                 ]);
    }

    /**
     * Test login returns avatar_url.
     */
    public function test_login_returns_avatar_url(): void
    {
        $user = User::create([
            'user_token' => 'avatar-test-token',
            'first_name' => 'Test',
            'last_name' => 'User',
            'surname' => 'Testovich',
            'educational_organization' => 'Org',
            'group' => 'G1',
            'course' => '1',
            'avatar' => 'avatars/test-avatar.jpg'
        ]);

        $response = $this->postJson('/api/login', [
            'user_token' => 'avatar-test-token',
        ]);

        $response->assertStatus(200)
                 ->assertJsonStructure([
                     'token',
                     'user' => ['avatar_url']
                 ]);
        
        $this->assertNotNull($response->json('user.avatar_url'));
        $this->assertStringContainsString('storage/avatars/test-avatar.jpg', $response->json('user.avatar_url'));
    }
}
